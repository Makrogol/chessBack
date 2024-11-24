from datetime import datetime
from enum import Enum

from sqlalchemy import Table, Column, Integer, String, TIMESTAMP, ForeignKey, JSON
from database import metadata


class GameResult(Enum):
    mate_to_white = "mate_to_white"
    mate_to_black = "mate_to_black"
    pat = "pat"
    draw = "draw"

# TODO переделать на класс (как с самой игрой или с юзером)
# Список всех игр: дата игры, кто играл за черных, кто за белых, результат (кто победил, ничья, пат)
game_history = Table(
    "game_history",
    metadata,
    Column("id", Integer, primary_key=True),
    Column("game_date", TIMESTAMP),
    Column("user_white", Integer, ForeignKey("user.id")),
    Column("user_black", Integer, ForeignKey("user.id")),
    Column("game_result", GameResult, nullable=False),
)