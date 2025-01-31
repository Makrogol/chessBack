from enum import Enum

class MoveType(Enum):
    CASTLING = 0
    PASSANT = 1
    MAGIC_PAWN_TRANSFORMATION = 2
    NOT_SPECIAL = 3
    NOT_MOVE = 4

    def __str__(self) -> str:
        return str(self.value)
