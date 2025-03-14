from pydantic import BaseModel


# WebSocket messages

class BaseMessage(BaseModel):
    username: str
    opponent_username: str

class TurnMessage(BaseMessage):
    turn: str

class GameEndMessage(BaseMessage):
    game_end: str

class GameStartMessage(BaseMessage):
    pass


# WebSocket received messages

class BaseReceivedMessage(BaseModel):
    username: str
    opponent_username: str

class TurnReceivedMessage(BaseReceivedMessage):
    turn: str

class GameEndReceivedMessage(BaseReceivedMessage):
    game_end: str

class GameStartReceivedMessage(BaseReceivedMessage):
    pass
