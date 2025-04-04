from starlette.websockets import WebSocket

from .schemas.websocket_messages_schemas import BaseMessage
from .schemas.websocket_schemas import WebSocketConnection, GameData


class WebSocketManager:
    def __init__(self):
        self.__connections: {str: WebSocketConnection} = {}

    async def connect(self, websocket: WebSocket, username: str) -> None:
        await websocket.accept()
        self.__connections[username] = WebSocketConnection(websocket=websocket)

    async def disconnect(self, username: str) -> None:
        if self.has_connection(username):
            self.__connections[username].websocket.close()
            del self.__connections[username]
        # # TODO можно сделать, чтобы не по сокету искалось, а по username
        # for username, connection in self.__connections.items():
        #     if websocket == connection.websocket:
        #         del self.__connections[username]
        #         return

    async def broadcast(self, data: BaseMessage) -> None:
        # broadcasting data to all connected clients
        # for connection in self.__connections.values():
        for username, connection in self.__connections.items():
            print(f"broadcast send data {data} to {username}")
            await connection.websocket.send_json(vars(data))

    async def send_to_user(self, username: str, data: BaseMessage) -> None:
        if self.has_connection(username):
            print(f"send data {data} to {username}")
            await self.__connections[username].websocket.send_json(vars(data))

    def remove_all_connections(self) -> None:
        self.__connections.clear()

    def remove_connection(self, username) -> None:
        if self.has_connection(username):
            del self.__connections[username]

    def update_game_data_on_start_game(self, username: str, opponent_username: str, main_color: str) -> None:
        if self.has_connection(username):
            self.__connections[username].game_data.username = username
            self.__connections[username].game_data.opponent_username = opponent_username
            self.__connections[username].game_data.main_color = main_color
        if self.has_connection(opponent_username):
            self.__connections[opponent_username].game_data.opponent_username = username
            self.__connections[opponent_username].game_data.useername = opponent_username
            # TODO надо выделить модуль для общения с ядром через питон в отдельное что-то и из него взять unparser
            #   и с ним уже работать с color это мега костыль
            self.__connections[opponent_username].game_data.main_color = "2" if main_color == "1" else "1"

    def update_game_data_on_turn(self, username: str, opponent_username: str, game_fen: str) -> None:
        if self.has_connection(username):
            self.__connections[username].game_data.game_fen = game_fen
            self.__connections[username].game_data.is_opponent_turn = True
        if self.has_connection(username):
            self.__connections[opponent_username].game_data.game_fen = game_fen
            self.__connections[opponent_username].game_data.is_opponent_turn = False

    def clear_game_data(self, username: str) -> None:
        if self.has_connection(username):
            self.__connections[username].game_data = GameData()

    def has_any_connection(self) -> bool:
        return self.__connections != {}

    def has_not_completed_game(self, opponent_username: str) -> bool:
        return self.get_not_completed_game_data(opponent_username) is not None

    def get_not_completed_game_data(self, opponent_username: str) -> GameData | None:
        for username in self.__connections.keys():
            if (self.__connections[username].game_data.opponent_username == opponent_username and
                    self.__connections[username].game_data.game_fen is not None):
                return self.__connections[username].game_data
        return None

    def has_connection(self, username: str | None) -> bool:
        return username is not None and username in self.__connections.keys()
