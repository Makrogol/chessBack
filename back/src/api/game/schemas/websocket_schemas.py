from pydantic import BaseModel, ConfigDict
from starlette.websockets import WebSocket


class BaseSchema(BaseModel):
    pass


class GameData(BaseSchema):
    # TODO зачем тут юзернейм
    username: str | None = None
    opponent_username: str | None = None
    game_fen: str | None = None
    is_opponent_turn: bool | None = None
    main_color: str | None = None
    is_switched_color: str | None = None
    is_play_with_bot: bool | None = None
    user_available: bool | None = None


class WebSocketConnection(BaseModel):
    model_config = ConfigDict(arbitrary_types_allowed=True)

    websocket: WebSocket
    game_data: GameData = GameData()
