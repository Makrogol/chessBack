import ctypes

from game.chess_parser import ChessParser
from game.chess_unparser import ChessUnparser
from game.move_type import MoveType
from game.color import Color
from game.game_state import GameState
from game.position import Position
from game.piece_type import PieceType
from game.board import BoardRepresentation

class CppApi():
    def __init__(self) -> None:
        self.parser = ChessParser()
        self.unparser = ChessUnparser()

        self.lib = ctypes.CDLL('/home/konstantin/Документы/python/python_cpp_connect/build/libcyclechesscpp.so')

        self.lib.startGame.argtypes = [ctypes.c_char_p]

        self.lib.getPossibleMovesForPosition.argtypes = [ctypes.c_char_p]
        self.lib.getPossibleMovesForPosition.restype = ctypes.c_char_p

        self.lib.tryDoMove.argtypes = [ctypes.c_char_p]
        self.lib.tryDoMove.restype = ctypes.c_char_p

        self.lib.getGameState.restype = ctypes.c_char_p

        self.lib.tryDoMagicPawnTransformation.argtypes = [ctypes.c_char_p]
        self.lib.tryDoMagicPawnTransformation.restype = ctypes.c_char_p

        self.lib.getKingPositionByColor.argtypes = [ctypes.c_char_p]
        self.lib.getKingPositionByColor.restype = ctypes.c_char_p

        self.lib.getBoardRepresentation.restype = ctypes.c_char_p

        self.lib.getCurrentTurn.restype = ctypes.c_char_p

        self.lib.startGameWithFen.argtypes = [ctypes.c_char_p, ctypes.c_char_p]

    def startGame(self, color: Color) -> None:
        self.lib.startGame(self.parser.color(color))

    def endGame(self) -> None:
        self.lib.endGame()
    
    def startGameWithFen(self, mainColor: Color, fen: str) -> None:
        self.lib.startGameWithFen(self.parser.color(mainColor), self.parser.fen(fen))

    def getCurrentTurn(self) -> Color:
        return self.unparser.color(self.lib.getCurrentTurn())

    def getPossibleMovesForPosition(self, position: Position) -> list[Position]:
        return self.unparser.possible_moves(self.lib.getPossibleMovesForPosition(self.parser.position(position)))

    def tryDoMove(self, position_first: Position, position_second: Position) -> MoveType:
        return self.unparser.move_type(self.lib.tryDoMove(self.parser.positionsToMove(position_first, position_second)))

    def getGameState(self) -> GameState:
        return self.unparser.game_state(self.lib.getGameState())

    def tryDoMagicPawnTransformation(self, position: Position, piece_type: PieceType) -> bool:
        return self.unparser.result_do_magic_pawn_transformation(self.lib.tryDoMagicPawnTransformation(self.parser.positionAndPieceTypeForMagicPawnTransformation(position, piece_type)))

    def getKingPositionByColor(self, color: Color) -> Position:
        return self.unparser.position(self.lib.getKingPositionByColor(self.parser.color(color)))

    def getBoardRepresentation(self) -> BoardRepresentation:
        return self.unparser.board_representation(self.lib.getBoardRepresentation())
