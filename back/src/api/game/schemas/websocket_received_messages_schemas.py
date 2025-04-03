from pydantic import BaseModel


class BaseReceivedMessage(BaseModel):
    username: str
    opponent_username: str

class TurnReceivedMessage(BaseReceivedMessage):
    turn: str
    game_fen: str

class GameEndReceivedMessage(BaseReceivedMessage):
    game_end: str

class GameStartReceivedMessage(BaseReceivedMessage):
    main_color: str
