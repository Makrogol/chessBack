from fastapi import APIRouter, WebSocket
from starlette.websockets import WebSocketDisconnect
import json

from .utils import data_reaction, send_user_available_state
from .web_socket_manager import WebSocketManager

router = APIRouter(prefix="/game", tags=["Game"])

manager = WebSocketManager()


@router.websocket("/{username}")
async def websocket_game(username: str, websocket: WebSocket):
    await manager.connect(websocket, username)
    await send_user_available_state(manager, username, True)
    try:
        while True:
            # here we are waiting for an oncomming message from clients
            data = await websocket.receive_text()
            data = json.loads(data)
            # precessing the incomming message
            await data_reaction(manager, data)
            if not manager.has_any_connection():
                return
    except WebSocketDisconnect:
        await manager.disconnect(websocket)
        await send_user_available_state(manager, username, False)
    except:
        pass
