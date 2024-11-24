from datetime import datetime

from pydantic import BaseModel

from game_history.models import GameResult


class GameHistoryCreate(BaseModel):
    id: int
    game_date: datetime
    user_white: int
    user_black: int
    game_result: GameResult