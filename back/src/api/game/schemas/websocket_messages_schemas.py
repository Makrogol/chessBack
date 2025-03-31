from pydantic import BaseModel


class BaseMessage(BaseModel):
    pass


class UsernameMessage(BaseMessage):
    username: str


class OpponentUsernameMessage(BaseMessage):
    opponent_username: str


class UserDataMessage(UsernameMessage, OpponentUsernameMessage):
    pass


class GameFenMessage(BaseMessage):
    game_fen: str


# TODO вообще не надо отправлять на андроид game_fen, это надо сначала на андроиде разделить messages и received_messages
class TurnMessage(UserDataMessage, GameFenMessage):
    turn: str


class GameEndMessage(UserDataMessage):
    game_end: str


class GameStartMessage(UserDataMessage):
    pass


class UserAvailableMessage(UsernameMessage):
    user_available: bool


class NotCompletedGameMessage(UserDataMessage, GameFenMessage):
    pass
