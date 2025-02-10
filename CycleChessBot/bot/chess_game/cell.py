
from chess_game.color import Color
from chess_game.piece import Piece
from chess_game.position import Position

class Cell():
    def __init__(self, color: Color, position: Position, piece: Piece | None = None) -> None:
        self.color = color
        self.piece = piece
        self.position = position

    def set_piece(self, piece: Piece | None) -> None:
        self.piece = None
        if piece is not None:
            self.piece = Piece(piece.piece_type, piece.color, self.position)

    def has_piece(self) -> bool:
        return self.piece is not None
