from .color import Color
from .position import Position
from .piece_type import PieceType
from .move import Move


class ChessParserStr:
    def color(self, color: Color) -> str:
        return "color:{}".format(color)

    def positionsToMove(self, position_first: Position, position_second: Position) -> str:
        return "positionsToMove:{};{}".format(position_first, position_second)

    def positionAndPieceTypeForMagicPawnTransformation(self, position: Position, piece_type: PieceType) -> str:
        return "positionAndPieceType:{};{}".format(position, piece_type)

    def position(self, position: Position) -> str:
        return str(position)

    def move(self, move: Move) -> str:
        return str(move)

    def fen(self, fen: str) -> str:
        return fen


class ChessParserByte:
    def __init__(self):
        self.str_parser = ChessParserStr()

    def color(self, color: Color) -> bytearray:
        return self.str_parser.color(color).encode("utf-8")

    def positionsToMove(
            self, position_first: Position, position_second: Position
    ) -> bytearray:
        return self.str_parser.positionsToMove(position_first, position_second).encode("utf-8")

    def positionAndPieceTypeForMagicPawnTransformation(
            self, position: Position, piece_type: PieceType
    ) -> bytearray:
        return self.str_parser.positionAndPieceTypeForMagicPawnTransformation(position, piece_type).encode("utf-8")

    def position(self, position: Position) -> bytearray:
        return self.str_parser.position(position).encode("utf-8")

    def move(self, move: Move) -> bytearray:
        return self.str_parser.move(move).encode("utf-8")

    def fen(self, fen: str) -> bytearray:
        return self.str_parser.fen(fen).encode("utf-8")
