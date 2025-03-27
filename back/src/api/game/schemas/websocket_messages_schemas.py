from pydantic import BaseModel


class BaseMessage(BaseModel):
    username: str
    opponent_username: str

class TurnMessage(BaseMessage):
    turn: str

class GameEndMessage(BaseMessage):
    game_end: str

class GameStartMessage(BaseMessage):
    pass
