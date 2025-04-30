import ctypes
from pathlib import Path

from .chess_parser import ChessParserByte
from .chess_unparser import ChessUnparserByte
from .move_type import MoveType
from .color import Color
from .game_state import GameState
from .position import Position
from .piece_type import PieceType
from .board import BoardRepresentation
from .move import Move

from ..core.config import BASE_DIR


class CppApi:
    def __init__(self) -> None:
        self.parser = ChessParserByte()
        self.unparser = ChessUnparserByte()

        self.lib = ctypes.CDLL(str(BASE_DIR / "src" / "cpp_bridge" / "build" / "libcyclechesscpp.so"))

        self.lib.startGame.argtypes = [ctypes.c_char_p]

        self.lib.getPossibleMovesForPosition.argtypes = [ctypes.c_char_p]
        self.lib.getPossibleMovesForPosition.restype = ctypes.c_char_p

        self.lib.tryDoMove.argtypes = [ctypes.c_char_p]
        self.lib.tryDoMove.restype = ctypes.c_char_p

        self.lib.tryDoMoveV2.argtypes = [ctypes.c_char_p]
        self.lib.tryDoMoveV2.restype = ctypes.c_char_p

        self.lib.getGameState.restype = ctypes.c_char_p

        self.lib.tryDoMagicPawnTransformation.argtypes = [ctypes.c_char_p]
        self.lib.tryDoMagicPawnTransformation.restype = ctypes.c_char_p

        self.lib.getKingPositionByColor.argtypes = [ctypes.c_char_p]
        self.lib.getKingPositionByColor.restype = ctypes.c_char_p

        self.lib.getBoardRepresentation.restype = ctypes.c_char_p

        self.lib.getCurrentTurn.restype = ctypes.c_char_p

        self.lib.startGameWithFen.argtypes = [ctypes.c_char_p, ctypes.c_char_p]

        self.lib.getFen.restype = ctypes.c_char_p

        self.lib.canDoOneStepAndDrawByFiftyMoves.restype = ctypes.c_char_p

        self.lib.canDoPassant.restype = ctypes.c_char_p

        self.lib.allPossibleMoves.restype = ctypes.c_char_p

    def startGame(self, color: Color) -> None:
        self.lib.startGame(self.parser.color(color))

    def endGame(self) -> None:
        self.lib.endGame()

    def startGameWithFen(self, mainColor: Color, fen: str) -> None:
        self.lib.startGameWithFen(self.parser.color(mainColor), self.parser.fen(fen))

    def getCurrentTurn(self) -> Color:
        return self.unparser.color(self.lib.getCurrentTurn())

    def getPossibleMovesForPosition(self, position: Position) -> list[Position]:
        return self.unparser.possible_positions(
            self.lib.getPossibleMovesForPosition(self.parser.position(position))
        )

    def tryDoMove(self, move: Move) -> MoveType:
        return self.unparser.move_type(self.lib.tryDoMoveV2(self.parser.move(move)))

    # def tryDoMove(self, position_first: Position, position_second: Position) -> MoveType:
    #     return self.unparser.move_type(self.lib.tryDoMove(self.parser.positionsToMove(position_first, position_second)))

    def getGameState(self) -> GameState:
        return self.unparser.game_state(self.lib.getGameState())

    def tryDoMagicPawnTransformation(
        self, position: Position, piece_type: PieceType
    ) -> bool:
        return self.unparser.result_do_magic_pawn_transformation(
            self.lib.tryDoMagicPawnTransformation(
                self.parser.positionAndPieceTypeForMagicPawnTransformation(
                    position, piece_type
                )
            )
        )

    def getKingPositionByColor(self, color: Color) -> Position:
        return self.unparser.position(
            self.lib.getKingPositionByColor(self.parser.color(color))
        )

    def getFen(self) -> str:
        return self.lib.getFen().decode("utf-8")

    def canDoOneStepAndDrawByFiftyMoves(self) -> bool:
        return self.unparser.result_can_do_one_move_and_draw_by_fifty_moves(
            self.lib.canDoOneStepAndDrawByFiftyMoves()
        )

    def canDoPassant(self) -> bool:
        return self.unparser.result_can_do_passant(self.lib.canDoPassant())

    def allPossibleMoves(self) -> list[Move]:
        return self.unparser.possible_moves(self.lib.allPossibleMoves())

    def getBoardRepresentation(self) -> BoardRepresentation:
        return self.unparser.board_representation(self.lib.getBoardRepresentation())
