from pydantic import BaseModel


class BaseMessage(BaseModel):
    pass


class UsernameMessage(BaseMessage):
    username: str


class OpponentUsernameMessage(BaseMessage):
    opponent_username: str


class UserDataMessage(UsernameMessage, OpponentUsernameMessage):
    pass


class TurnMessage(UserDataMessage):
    turn: str


class GameEndMessage(UserDataMessage):
    game_end: str


class GameStartMessage(UserDataMessage):
    pass


class UserAvailableMessage(UsernameMessage):
    user_available: bool


class NotCompletedGameMessage(UserDataMessage):
    game_fen: str
