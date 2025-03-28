from pydantic import BaseModel


class BaseMessage(BaseModel):
    pass


class UsernameMessage(BaseMessage):
    username: str


class UserDataMessage(UsernameMessage):
    opponent_username: str


class TurnMessage(UserDataMessage):
    turn: str


class GameEndMessage(UserDataMessage):
    game_end: str


class GameStartMessage(UserDataMessage):
    pass


class UserAvailableMessage(UsernameMessage):
    user_available: bool
