from .piece_type import PieceType
from .color import Color


class PieceTypeAndColor:
    def __init__(self, piece_type: PieceType, color: Color) -> None:
        self.piece_type = piece_type
        self.color = color
