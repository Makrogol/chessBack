import config
from re import A
import numpy as np

import time
import logging

from chess_game.cpp_api import CppApi
from chess_game.board import Board
from chess_game.color import Color, get_all_colors
from chess_game.piece_type import PieceType, get_all_piece_types
from chess_game.move import Move

logging.basicConfig(level=logging.INFO, filename="_log_.log", format=' %(message)s')


class ChessEnv:
    def __init__(self, fen: str = config.DEFAULT_FEN):
        """
        Initialize the chess environment
        """
        # the chessboard
        self.fen = fen
        self.cpp_api = CppApi()
        self.reset()
    
    def reset(self):
        if self.cpp_api.getFen() != self.fen:
            self.cpp_api.startGameWithFen(Color.WHITE, self.fen)

    @staticmethod
    def state_to_input(fen: str) -> np.ndarray(config.INPUT_SHAPE):
        """
        Convert board to a fen that is interpretable by the model
        """
        cpp_api = CppApi()
        # Какой будет mainColor? 
        # возможно просто надо сделать, чтобы всегда был белый
        if cpp_api.getFen() != fen:
            cpp_api.startGameWithFen(Color.WHITE, fen)
        board = Board()
        board.create_from_fen(fen)

        # 1. is it white's turn? (1x8x8)
        is_white_turn = np.ones((8, 8)) if board.turn_color == Color.WHITE else np.zeros((8, 8))

        # 2. castling rights (4x8x8)
        castling = np.asarray([
            np.ones((8, 8)) if board.castling_rights[0] else np.zeros((8, 8)),
            np.ones((8, 8)) if board.castling_rights[1] else np.zeros((8, 8)),
            np.ones((8, 8)) if board.castling_rights[2] else np.zeros((8, 8)),
            np.ones((8, 8)) if board.castling_rights[3] else np.zeros((8, 8)),
        ])

        # 3. repitition counter
        counter = np.ones((8, 8)) if cpp_api.canDoOneStepAndDrawByFiftyMoves() else np.zeros((8, 8))

        # create new np array
        arrays = []
        for color in get_all_colors():
            # 4. player 1's pieces (6x8x8)
            # 5. player 2's pieces (6x8x8)
            for piece_type in get_all_piece_types():
                # 6 arrays of 8x8 booleans
                array = np.zeros((8, 8))
                for piece in board.get_pieces_by_type_and_color(color, piece_type):
                    array[piece.position.i][piece.position.j] = True
                arrays.append(array)
        arrays = np.asarray(arrays)

        # 6. en passant square (8x8)
        en_passant = np.zeros((8, 8))
        if cpp_api.canDoPassant():
            en_passant[board.passant_position][board.passant_position] = True

        r = np.array([is_white_turn, *castling,
                     counter, *arrays, en_passant]).reshape((1, *config.INPUT_SHAPE))
        # memory management
        return r.astype(bool)

    @staticmethod
    def estimate_winner(board: Board) -> int:
        """
        Estimate the winner of the current node.
        Pawn = 1, Bishop = 3, Rook = 5, Queen = 9
        Positive score = white wins, negative score = black wins
        """
        score = 0
        piece_scores = {
            PieceType.PAWN: 1,
            PieceType.KNIGHT: 3,
            PieceType.BISHOP: 3,
            PieceType.ROOK: 5,
            PieceType.QUEEN: 9,
            PieceType.KING: 0
        }
        for piece in board.get_all_pieces():
            if piece.color == Color.WHITE:
                score += piece_scores[piece.piece_type]
            else:
                score -= piece_scores[piece.piece_type]
        if np.abs(score) > 5:
            if score > 0:
                logging.info("<chess_env> White wins (estimated)")
                return 0.25
            else:
                logging.info("<chess_env> Black wins (estimated)")
                return -0.25
        else:
            logging.info("<chess_env> Draw")
            return 0

    @staticmethod
    def get_piece_amount(board: Board) -> int:
        return len(board.get_all_pieces())

    def __str__(self):
        """
        Print the board
        """
        logging.info('<chess_env> __str__ if used need to fill')
        return ""

    def step(self, move: Move):
        """
        Perform a step in the game
        """
        # Уверены, что поле не менялось в либе
        # Хотя непонятно с чего мы в этом так уверены
        if self.cpp_api.getFen() != self.fen:
            self.cpp_api.startGameWithFen(Color.WHITE, self.fen)
        self.cpp_api.tryDoMove(move)
        self.fen = self.cpp_api.getFen()
