from starlette.websockets import WebSocket

from .shemas import BaseMessage


class WebSocketManager:
    def __init__(self):
        self.__connections: {str: WebSocket} = {}


    async def connect(self, websocket: WebSocket, username: str) -> None:
        await websocket.accept()
        self.__connections[username] = websocket


    async def disconnect(self, websocket: WebSocket) -> None:
        for username, socket in self.__connections.items():
            if websocket == socket:
                del self.__connections[username]
                return


    async def broadcast(self, data: BaseMessage) -> None:
        # broadcasting data to all connected clients
        for connection in self.__connections.values():
            await connection.send_json(vars(data))


    async def send_to_user(self, username: str, data: BaseMessage) -> None:
        if username in self.__connections.keys():
            print("data = ", vars(data))
            await self.__connections[username].send_json(vars(data))


    def remove_all_connections(self) -> None:
        self.__connections.clear()


    def remove_connection(self, username) -> None:
        if username in self.__connections.keys():
            del self.__connections[username]


    def has_any_connection(self) -> bool:
        return self.__connections != {}


    def has_connection(self, username: str | None) -> bool:
        return username is not None and username in self.__connections.keys()
