from enum import Enum

class PieceType(Enum):
    EMPTY = 0
    PAWN = 1
    KING = 2
    ROOK = 3
    BISHOP = 4
    QUEEN = 5
    KNIGHT = 6

    def __str__(self) -> str:
        return str(self.value)