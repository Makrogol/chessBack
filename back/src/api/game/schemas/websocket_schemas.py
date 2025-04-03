from pydantic import BaseModel, ConfigDict
from starlette.websockets import WebSocket


class BaseSchema(BaseModel):
    pass


class GameData(BaseSchema):
    opponent_username: str | None = None
    game_fen: str | None = None
    is_opponent_turn: str | None = None


class WebSocketConnection(BaseModel):
    model_config = ConfigDict(arbitrary_types_allowed=True)

    websocket: WebSocket
    game_data: GameData = GameData()
