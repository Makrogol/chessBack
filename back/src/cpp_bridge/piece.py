from .piece_type import PieceType
from .color import Color
from .position import Position


class Piece:
    def __init__(self, piece_type: PieceType, color: Color, position: Position) -> None:
        self.piece_type = piece_type
        self.color = color
        self.count_steps = 0
        self.position = position
