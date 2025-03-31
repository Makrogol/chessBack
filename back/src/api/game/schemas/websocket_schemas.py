from pydantic import BaseModel
from starlette.websockets import WebSocket


class BaseSchema(BaseModel):
    pass


class GameData(BaseSchema):
    opponent_username: str | None = None
    game_fen: str | None = None


class WebSocketConnection(BaseSchema, GameData):
    websocket: WebSocket
