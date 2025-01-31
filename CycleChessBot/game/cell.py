
from game.color import Color
from game.piece import Piece
from game.position import Position

class Cell():
    def __init__(self, color: Color, position: Position, piece: Piece | None = None) -> None:
        self.color = color
        self.piece = piece
        self.position = position
    
    def set_piece(self, piece: Piece | None) -> None:
        self.piece = None
        if piece is not None:
            self.piece = Piece(piece.piece_type, piece.color)
