
from game.piece_type import PieceType
from game.color import Color

class Piece():
    def __init__(self, piece_type: PieceType, color: Color) -> None:
        self.piece_type = piece_type
        self.color = color
