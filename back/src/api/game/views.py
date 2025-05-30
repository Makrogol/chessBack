from fastapi import APIRouter, WebSocket
from starlette.websockets import WebSocketDisconnect
import json

from .utils import (
    data_reaction,
    send_user_available_state,
    send_user_has_not_completed_game,
)
from .web_socket_manager import WebSocketManager
import datetime

router = APIRouter(prefix="/game", tags=["Game"])

manager = WebSocketManager()


@router.websocket("/{username}")
async def websocket_game(username: str, websocket: WebSocket):
    try:
        await manager.connect(websocket, username)
        await send_user_available_state(manager, username, True)

        if manager.has_not_completed_game(username):
            print(f"{datetime.datetime.now()} Game {username} has not completed yet")
            await send_user_has_not_completed_game(manager, username)

        while True:
            # here we are waiting for an oncomming message from clients
            data = await websocket.receive_text()
            print(f"{datetime.datetime.now()} data from {username} ", data)
            data = json.loads(data)
            # precessing the incomming message
            await data_reaction(manager, data)
            if not manager.has_any_connection():
                return
    except WebSocketDisconnect:
        await manager.disconnect(username)
        await send_user_available_state(manager, username, False)
    except:
        pass
