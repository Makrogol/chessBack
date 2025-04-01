from starlette.websockets import WebSocket

from .schemas.websocket_messages_schemas import BaseMessage
from .schemas.websocket_schemas import WebSocketConnection, GameData


class WebSocketManager:
    def __init__(self):
        self.__connections: {str: WebSocketConnection} = {}

    async def connect(self, websocket: WebSocket, username: str) -> None:
        await websocket.accept()
        self.__connections[username] = WebSocketConnection(websocket=websocket)

    async def disconnect(self, websocket: WebSocket) -> None:
        # TODO можно сделать, чтобы не по сокету искалось, а по никнейму
        for username, connection in self.__connections.items():
            if websocket == connection.websocket:
                del self.__connections[username]
                return

    async def broadcast(self, data: BaseMessage) -> None:
        # broadcasting data to all connected clients
        for connection in self.__connections.values():
        # for username, connection in self.__connections.items():
            await connection.websocket.send_json(vars(data))

    async def send_to_user(self, username: str, data: BaseMessage) -> None:
        if self.has_connection(username):
            await self.__connections[username].websocket.send_json(vars(data))

    def remove_all_connections(self) -> None:
        self.__connections.clear()

    def remove_connection(self, username) -> None:
        if self.has_connection(username):
            del self.__connections[username]

    def set_opponent(self, username: str, opponent_username: str) -> None:
        if self.has_connection(username):
            self.__connections[username].opponent_username = opponent_username
        if self.has_connection(opponent_username):
            self.__connections[opponent_username].opponent_username = username

    def update_game_fen(self, username: str, game_fen: str) -> None:
        if self.has_connection(username):
            self.__connections[username].game_fen = game_fen

    def clear_game_data(self, username: str) -> None:
        if self.has_connection(username):
            self.__connections[username].game_fen = None
            self.__connections[username].opponent_username = None

    def has_any_connection(self) -> bool:
        return self.__connections != {}

    def has_not_completed_game(self, opponent_username: str) -> bool:
        return self.get_not_completed_game(opponent_username) is not None

    def get_not_completed_game(self, opponent_username: str) -> GameData | None:
        for username in self.__connections.keys():
            if (self.__connections[username].opponent_username == opponent_username and
                    self.__connections[username].game_fen is not None):
                return GameData(opponent_username=username, game_fen=self.__connections[username].game_fen)
        return None

    def has_connection(self, username: str | None) -> bool:
        return username is not None and username in self.__connections.keys()
