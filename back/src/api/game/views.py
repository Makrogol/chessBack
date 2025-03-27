from fastapi import APIRouter, WebSocket
from starlette.websockets import WebSocketDisconnect
import json

from .schemas.responses_schemas import UserAvailableResponse
from .utils import data_reaction
from .. import manager

router = APIRouter(prefix="/game", tags=["Game"])


# TODO это можно убрать, либо совсем, либо в auth



@router.websocket("/{username}")
async def websocket_game(username: str, websocket: WebSocket):
    await manager.connect(websocket, username)
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
    except:
        pass
