import os
import time
from .chessEnv import ChessEnv
from .agent import Agent
from .utils import *
import logging
from .config import *
from .node import Edge
from .mcts import MCTS
import uuid
import pandas as pd
import numpy as np

from ..cpp_bridge.color import Color
from ..cpp_bridge.game_state import is_game_over, GameState
from ..cpp_bridge.board import Board

logging.basicConfig(level=logging.INFO, filename="_log_.log", format=" %(message)s")


class Game:
    def __init__(self, env: ChessEnv, white: Agent, black: Agent):
        """
        The Game class is used to play chess games between two agents.
        """
        self.env = env
        self.white = white
        self.black = black

        self.memory = []

        self.reset()

    def reset(self):
        self.env.reset()
        if self.env.cpp_api.getFen() != self.env.fen:
            self.env.cpp_api.startGameWithFen(Color.WHITE, self.env.fen)
        self.turn = (
            self.env.cpp_api.getCurrentTurn() == Color.WHITE
        )  # True = white, False = black

    @staticmethod
    def get_winner(result: GameState) -> int:
        return (
            1
            if result == GameState.MATE_FOR_BLACK
            else -1 if result == GameState.MATE_FOR_WHITE else 0
        )

    @time_function
    def play_one_game(self, stochastic: bool = True) -> int:
        """
        Play one game from the starting position, and save it to memory.
        Keep playing moves until either the game is over, or it has reached the move limit.
        If the move limit is reached, the winner is estimated.
        """
        # reset everything
        self.reset()
        # add a new memory entry
        self.memory.append([])
        # show the board
        logging.info(f"\n<game>{self.env.cpp_api.getFen()}")
        # counter to check amount of moves played. if above limit, estimate winner
        counter, previous_edges, full_game = 0, (None, None), True
        while not is_game_over(self.env.cpp_api.getGameState()):
            # play one move (previous move is used for updating the MCTS tree)
            previous_edges = self.play_move(
                stochastic=stochastic, previous_moves=previous_edges
            )
            logging.info(f"\n<game> {self.env.cpp_api.getFen()}")
            logging.info(
                f"<game> Value according to white: {self.white.mcts.root.value}"
            )
            logging.info(
                f"<game> Value according to black: {self.black.mcts.root.value}"
            )
            # if os.environ.get("SELFPLAY_SHOW_BOARD") == "true":
            #     self.GUI.gameboard.board.set_fen(self.env.board.fen())
            #     self.GUI.draw()

            # end if the game drags on too long
            counter += 1
            if counter > config.MAX_GAME_MOVES:
                # estimate the winner based on piece values
                board = Board()
                if self.env.cpp_api.getFen() != self.env.fen:
                    self.env.cpp_api.startGameWithFen(Color.WHITE, self.env.fen)
                board.create_from_fen(self.env.cpp_api.getFen())
                winner = ChessEnv.estimate_winner(board)
                logging.info(
                    f"<game> Game over by move limit ({config.MAX_GAME_MOVES}). Result: {winner}"
                )
                full_game = False
                break
        if full_game:
            # get the winner based on the result of the game
            winner = Game.get_winner(self.env.cpp_api.getGameState())
            logging.info(f"<game> Game over. Result: {winner}")
        # save game result to memory for all games
        for index, element in enumerate(self.memory[-1]):
            self.memory[-1][index] = (element[0], element[1], winner)

        # Пока что без логов, потом надо будет раскомментить
        # game = ChessGame()
        # # set starting position
        # game.setup(self.env.fen)
        # # add moves
        # node = game.add_variation(self.env.board.move_stack[0])
        # for move in self.env.board.move_stack[1:]:
        #     node = node.add_variation(move)
        # # print pgn
        # logging.info(game)

        # save memory to file
        self.save_game(name="game", full_game=full_game)

        return winner

    def play_move(
        self,
        stochastic: bool = True,
        previous_moves: tuple[Edge, Edge] = (None, None),
        save_moves=True,
    ):
        """
        Play one move. If stochastic is True, the move is chosen using a probability distribution.
        Otherwise, the move is chosen based on the highest N (deterministically).
        The previous moves are used to reuse the MCTS tree (if possible): the root node is set to the
        node found after playing the previous moves in the current tree.
        """
        # whose turn is it
        current_player = self.white if self.turn else self.black

        if previous_moves[0] is None or previous_moves[1] is None:
            # create new tree with root node == current board
            current_player.mcts = MCTS(
                current_player, fen=self.env.fen, stochastic=stochastic
            )
        else:
            # change the root node to the node after playing the two previous moves
            try:
                node = current_player.mcts.root.get_edge(
                    previous_moves[0].move
                ).output_node
                node = node.get_edge(previous_moves[1].move).output_node
                current_player.mcts.root = node
            except AttributeError:
                logging.warning(
                    "<game> Node does not exist in tree, continuing with new tree..."
                )
                current_player.mcts = MCTS(
                    current_player, fen=self.env.fen, stochastic=stochastic
                )
        # play n simulations from the root node
        current_player.run_simulations(n=config.SIMULATIONS_PER_MOVE)

        moves = current_player.mcts.root.edges

        if save_moves:
            self.save_to_memory(self.env.fen, moves)

        sum_move_visits = sum(e.N for e in moves)
        probs = [e.N / sum_move_visits for e in moves]

        if stochastic:
            # choose a move based on a probability distribution
            best_move = np.random.choice(moves, p=probs)
        else:
            # choose a move based on the highest N
            best_move = moves[np.argmax(probs)]

        # TODO Очень фигово создавать доску каждый раз
        # Надо либо ее один раз создать где-то раньше
        # Либо просверлить дырку в либе для числа ходов
        board = Board()
        board.create_from_fen(self.env.fen)
        # play the move
        logging.info(
            f"<game> {'White' if self.turn else 'Black'} played  {board.countMoves} {best_move.move}"
        )
        self.env.step(best_move.move)

        # switch turn
        self.turn = not self.turn

        # return the previous move and the new move
        return previous_moves[1], best_move

    def save_to_memory(self, fen: str = config.DEFAULT_FEN, moves=None) -> None:
        """
        Append the current fen and move probabilities to the internal memory.
        """
        sum_move_visits = sum(e.N for e in moves)
        # create dictionary of moves and their probabilities
        search_probabilities = {str(e.move): e.N / sum_move_visits for e in moves}
        # winner gets added after game is over
        self.memory[-1].append((fen, search_probabilities, None))

    def save_game(self, name: str = "game", full_game: bool = False) -> None:
        """
        Save the internal memory to a .npy file.
        """
        # the game id consist of game + datetime
        game_id = f"{name}-{str(uuid.uuid4())[:8]}"
        if full_game:
            # if the game result was not estimated, save the game id to a seperate file (to look at later)
            with open("memory/full_games.txt", "a") as f:
                f.write(f"{game_id}.npy\n")
        np.save(os.path.join(config.MEMORY_DIR, game_id), self.memory[-1])
        logging.info(
            f"<game> Game saved to {os.path.join(config.MEMORY_DIR, game_id)}.npy"
        )
        logging.info(f"<game> Memory size: {len(self.memory)}")

    @time_function
    def train_puzzles(self, puzzles: pd.DataFrame):
        """
        Create positions from puzzles (fen strings) and let the MCTS figure out how to solve them.
        The saved positions can be used to train the neural network.
        """
        logging.info(f"<game> Training on {len(puzzles)} puzzles")
        for puzzle in puzzles.itertuples():
            self.env.fen = puzzle.fen
            self.env.reset()
            # play the first move
            moves = puzzle.moves.split(" ")
            if self.env.cpp_api.getFen() != self.env.fen:
                self.env.cpp_api.startGameWithFen(Color.WHITE, self.env.fen)
            self.env.cpp_api.tryDoMove(moves.pop(0))
            logging.info(
                f"<game> Puzzle to solve ({puzzle.rating} ELO): {self.env.fen}"
            )
            logging.info(f"<game> Correct solution: {moves} ({len(moves)} moves)")
            self.memory.append([])
            counter, previous_edges = 0, (None, None)
            while not is_game_over(self.env.cpp_api.getGameState()):
                # deterministically choose the next move (we want no exploration here)
                previous_edges = self.play_move(
                    stochastic=False, previous_moves=previous_edges
                )
                if self.env.cpp_api.getFen() != self.env.fen:
                    self.env.cpp_api.startGameWithFen(Color.WHITE, self.env.fen)
                logging.info(
                    f"<game> Value according to white: {self.white.mcts.root.value}"
                )
                logging.info(
                    f"<game> Value according to black: {self.black.mcts.root.value}"
                )
                counter += 1
                if counter > config.MAX_PUZZLE_MOVES:
                    logging.warning("Puzzle could not be solved within the move limit")
                    break
            if self.env.cpp_api.getFen() != self.env.fen:
                self.env.cpp_api.startGameWithFen(Color.WHITE, self.env.fen)
            if not is_game_over(self.env.cpp_api.getGameState()):
                continue
            logging.info(
                f"<game> Puzzle complete. Ended after {counter} moves: {self.env.cpp_api.getGameState()}"
            )
            # save game result to memory for all games
            winner = Game.get_winner(self.env.cpp_api.getGameState())
            for index, element in enumerate(self.memory[-1]):
                self.memory[-1][index] = (element[0], element[1], winner)

            # TODO потом вернуть логи, когда разберусь как они работают
            # game = ChessGame()
            # # set starting position
            # game.setup(self.env.fen)
            # # add moves
            # node = game.add_variation(self.env.board.move_stack[0])
            # for move in self.env.board.move_stack[1:]:
            #     logging.info(move)
            #     node = node.add_variation(move)
            # # print pgn
            # logging.info(game)

            # save memory to file
            self.save_game(name="puzzle")

    @staticmethod
    def create_puzzle_set(filename: str, type: str = "mateIn2") -> pd.DataFrame:
        """
        Load the puzzles from a csv file. The type of puzzle can be specified.
        Return the puzzles as a Pandas DataFrame.
        """
        start_time = time.time()
        puzzles: pd.DataFrame = pd.read_csv(filename, header=None)
        # drop unnecessary columns
        puzzles = puzzles.drop(columns=[0, 4, 5, 6, 8])
        # set column names
        puzzles.columns = ["fen", "moves", "rating", "type"]
        # only keep puzzles where type contains "mate"
        puzzles = puzzles[puzzles["type"].str.contains(type)]
        logging.info(f"<game> Created puzzles in {time.time() - start_time} seconds")
        return puzzles
