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
    is_switched_color: str
    is_play_with_bot: str


class GameDeclineReceivedMessage(BaseReceivedMessage):
    decline_reason: str
