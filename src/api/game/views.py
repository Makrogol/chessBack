from fastapi import APIRouter, WebSocket
from starlette.websockets import WebSocketDisconnect
import json

router = APIRouter(prefix="/game", tags=["Game"])


class ConnectionManager:
    def __init__(self):
        self.connections: {str: WebSocket} = {}

    async def connect(self, websocket: WebSocket, username: str):
        await websocket.accept()
        self.connections[username] = websocket

    async def disconnect(self, websocket: WebSocket):
        for username, socket in self.connections.items():
            if websocket == socket:
                del self.connections[username]
                return

    async def broadcast(self, data: dict):
        # broadcasting data to all connected clients
        for connection in self.connections:
            await connection.send_json(data)


async def parse_data(manager: ConnectionManager, data: dict):
    if data["turn"] is not None:  # Сама игра
        await manager.connections[data["opponent_username"]].send_json(
            {
                "turn": data["turn"],
                "opponent_username": data["username"],
            }
        )
    elif data["game_end"] is not None:  # Завершение игры
        await manager.broadcast(
            {
                "opponent_username": data["username"],
                "game_end": data["game_end"],
            }
        )
        manager.connections = {}
    elif data["opponent_username"] in manager.connections.keys():  # Вызов на игру
        await manager.connections[data["opponent_username"]].send_json(
            {
                "opponent_username": data["username"],
            }
        )


manager = ConnectionManager()


@router.websocket("/{username}")
async def websocket_game(username: str, websocket: WebSocket):
    await manager.connect(websocket, username)
    print(username, "connected")
    try:
        while True:
            # here we are waiting for an oncomming message from clients
            data = await websocket.receive_text()
            print("string data", data)
            data = json.loads(data)
            print("json data", data)
            # precessing the incomming message
            await parse_data(manager, data)
            if len(manager.connections) == 0:
                return
    except WebSocketDisconnect:
        await manager.disconnect(websocket)
    except:
        pass
