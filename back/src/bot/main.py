import threading
import time
import numpy as np
from .chessEnv import ChessEnv
from .game import Game
from .agent import Agent
import argparse
import logging

from GUI.display import GUI

from ..cpp_bridge.color import Color
from ..cpp_bridge.game_state import is_game_over

logging.basicConfig(level=logging.INFO, filename="_log_.log", format=" %(message)s")


class Main:
    def __init__(
        self, player: bool, local_predictions: bool = True, model_path: str = None
    ):
        self.player = player

        # create an agent for the opponent
        self.opponent = Agent(
            local_predictions=local_predictions, model_path=model_path
        )

        if self.player:
            self.game = Game(ChessEnv(), None, self.opponent)
        else:
            self.game = Game(ChessEnv(), self.opponent, None)

        print("*" * 50)
        print(f"You play the {'white' if self.player else 'black'} pieces!")
        print("*" * 50)

        # previous moves (for the opponent's MCTS)
        self.previous_moves = (None, None)

        # gui on main thread
        self.GUI = GUI(800, 800, player, self.game.env.fen)

        # create separate thread for game logic
        thread = threading.Thread(target=self.play_game)
        thread.start()

        self.GUI_loop()

    def GUI_loop(self):
        while True:
            self.GUI.draw()

    def play_game(self):
        self.game.reset()
        winner = None
        while winner is None:
            if self.player == self.game.turn:
                self.get_player_move()
                self.game.turn = not self.game.turn
            else:
                self.opponent_move()
                # TODO пока этот файл не запускать, пока не разберусь, почему тут берется последний ход
                # self.GUI.make_move(self.game.env.board.move_stack[-1])
            # check if the game is over
            if self.game.env.cpp_api.getFen() != self.game.env.fen:
                self.game.env.cpp_api.startGameWithFen(Color.WHITE, self.game.env.fen)
            game_state = self.game.env.cpp_api.getGameState()
            if is_game_over(game_state):
                # get the winner
                winner = Game.get_winner(game_state)
                # show the winner but as string literal
                print(
                    "White wins"
                    if winner == 1
                    else "Black wins" if winner == -1 else "Draw"
                )

    def get_player_move(self):
        while True:
            time.sleep(0.2)
            # break when the player has made a move
            if self.GUI.move is not None:
                if self.game.env.cpp_api.getFen() != self.game.env.fen:
                    self.game.env.cpp_api.startGameWithFen(
                        Color.WHITE, self.game.env.fen
                    )
                self.game.env.cpp_api.tryDoMove(self.GUI.move)
                self.GUI.move = None
                break

    def opponent_move(self):
        self.GUI.gameboard.selected_position = None
        self.previous_moves = self.game.play_move(
            stochastic=False, previous_moves=self.previous_moves, save_moves=False
        )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--player",
        type=str,
        default=None,
        choices=("white", "black"),
        help="Whether to play as white or black. No argument means random.",
    )
    parser.add_argument(
        "--local-predictions",
        action="store_true",
        help="Use local predictions instead of the server",
    )
    parser.add_argument(
        "--model",
        type=str,
        default=None,
        help="For local predictions: specify the path to the model to use.",
    )
    args = parser.parse_args()
    args = vars(args)

    if args["local_predictions"]:
        if args["model"] is None:
            print("When using local predictions, specify the path to the model to use.")
            exit(1)

    model_path = args["model"]
    local_predictions = args["local_predictions"]

    if args["player"]:
        player = args["player"].lower().strip() == "white"
    else:
        player = np.random.choice([True, False])

    m = Main(player, local_predictions, model_path)
