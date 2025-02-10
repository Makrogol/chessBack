from chess_game.position import Position
from chess_game.piece_type import PieceType

class Move:
    def __init__(self, position_first: Position, position_second: Position, promotion: PieceType | None = None) -> None:
        self.position_first = position_first
        self.position_second = position_second
        self.promotion = promotion

    def __str__(self) -> str:
        if self.promotion is not None:
            return f"{self.position_first}.{self.position_second}.{self.promotion}"
        return f"{self.position_first}.{self.position_second}"
