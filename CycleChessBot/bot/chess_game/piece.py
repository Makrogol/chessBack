
from chess_game.piece_type_and_color import PieceTypeAndColor
from chess_game.piece_type import PieceType
from chess_game.color import Color
from chess_game.position import Position

class Piece():
    def __init__(self, piece_type: PieceType, color: Color, position: Position) -> None:
        self.piece_type = piece_type
        self.color = color
        self.count_steps = 0
        self.position = position
