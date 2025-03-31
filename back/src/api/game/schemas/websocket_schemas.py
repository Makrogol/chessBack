from pydantic import BaseModel, ConfigDict
from starlette.websockets import WebSocket


class BaseSchema(BaseModel):
    pass


class GameData(BaseSchema):
    opponent_username: str | None = None
    game_fen: str | None = None


class WebSocketConnection(GameData):
    model_config = ConfigDict(arbitrary_types_allowed=True)

    websocket: WebSocket
