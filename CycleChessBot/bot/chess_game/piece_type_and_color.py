from chess_game.piece_type import PieceType
from chess_game.color import Color

class PieceTypeAndColor():
    def __init__(self, piece_type: PieceType, color: Color) -> None:
        self.piece_type = piece_type
        self.color = color
