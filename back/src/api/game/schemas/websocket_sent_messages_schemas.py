from pydantic import BaseModel


class BaseSentMessage(BaseModel):
    pass


class UsernameSentMessage(BaseSentMessage):
    username: str


class OpponentUsernameSentMessage(BaseSentMessage):
    opponent_username: str


class UserDataSentMessage(UsernameSentMessage, OpponentUsernameSentMessage):
    pass


class GameFenSentMessage(BaseSentMessage):
    game_fen: str


class TurnSentMessage(UserDataSentMessage):
    turn: str


class GameEndSentMessage(UserDataSentMessage):
    game_end: str


class GameStartSentMessage(UserDataSentMessage):
    main_color: str
    is_switched_color: str


class UserAvailableSentMessage(UsernameSentMessage):
    user_available: bool


class NotCompletedGameSentMessage(GameStartSentMessage, GameFenSentMessage):
    is_opponent_turn: bool
    is_play_with_bot: bool


class GameDeclineSentMessage(UserDataSentMessage):
    decline_reason: str
