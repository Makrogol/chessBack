from starlette.websockets import WebSocket
import copy

from .schemas.websocket_sent_messages_schemas import BaseSentMessage
from .schemas.websocket_schemas import WebSocketConnection, GameData
import datetime
from ...cpp_bridge.chess_unparser import ChessUnparserByte
from ...cpp_bridge.chess_parser import ChessParserStr
from ...cpp_bridge.color import get_another_color

parser = ChessParserStr()
unparser = ChessUnparserByte()


class WebSocketManager:
    def __init__(self):
        self.__connections: {str: WebSocketConnection} = {}

    async def connect(self, websocket: WebSocket, username: str) -> None:
        await websocket.accept()
        self.__connections[username] = WebSocketConnection(websocket=websocket)

    async def disconnect(self, username: str) -> None:
        if self.is_user_available(username):
            self.__connections[username].game_data.user_available = False
        # try:
        #     if self.has_connection(username):
        #         print(f"{datetime.datetime.now()} disconnect {username}")
        #         await self.__connections[username].websocket.close()
        #         del self.__connections[username]
        # except Exception as e:
        #     print(f"{datetime.datetime.now()} disconnect {username} exception {e}")
        #     del self.__connections[username]

    async def broadcast(self, data: BaseSentMessage) -> None:
        for username, connection in self.__connections.items():
            print(f"{datetime.datetime.now()} broadcast send data {data} to {username}")
            try:
                if self.is_user_available(username):
                    await connection.websocket.send_json(vars(data))
            except Exception as e:
                print(
                    f"{datetime.datetime.now()} broadcast send to {username} exception {e}"
                )

    async def send_to_user(self, username: str, data: BaseSentMessage) -> None:
        try:
            if self.is_user_available(username):
                print(f"{datetime.datetime.now()} send data {data} to {username}")
                await self.__connections[username].websocket.send_json(vars(data))
        except Exception as e:
            print(f"{datetime.datetime.now()} send_to_user {username} exception {e}")

    async def remove_all_connections(self) -> None:
        for username in self.__connections.keys():
            await self.disconnect(username)

    # TODO вообще вот это не работа уже сокет манагера, он должен только отправлять и получать сообщения от сокета,
    #  это надо вынести куда-то в другое место
    def is_user_in_game(self, username: str) -> bool:
        return (
                self.has_connection(username)
                and self.__connections[username].game_data.opponent_username is not None
        )

    def update_game_data_for_opponent(
            self, opponent_username: str, game_data: GameData
    ) -> None:
        if self.has_connection(opponent_username):
            self.__connections[opponent_username].game_data = copy.deepcopy(game_data)
            self.__connections[opponent_username].game_data.username = opponent_username
            self.__connections[opponent_username].game_data.opponent_username = game_data.username
            self.__connections[opponent_username].game_data.is_opponent_turn = not game_data.is_opponent_turn
            self.__connections[opponent_username].game_data.main_color = parser.color(
                get_another_color(unparser.color_str(game_data.main_color))
            )
            self.__connections[opponent_username].game_data.is_switched_color = game_data.is_switched_color
            self.__connections[opponent_username].game_data.is_play_with_bot = game_data.is_play_with_bot

    def update_game_data_on_start_game(
            self,
            username: str,
            opponent_username: str,
            main_color: str,
            is_switched_color: str,
            is_play_with_bot: bool,
    ) -> None:
        if self.has_connection(username):
            self.__connections[username].game_data.username = username
            self.__connections[username].game_data.opponent_username = opponent_username
            self.__connections[username].game_data.main_color = main_color
            self.__connections[username].game_data.is_switched_color = is_switched_color
            self.__connections[username].game_data.is_play_with_bot = is_play_with_bot

    def is_user_play_with_bot(self, username: str) -> bool:
        return self.has_connection(username) and self.__connections[username].game_data.is_play_with_bot

    def update_game_data_on_turn(
            self, username: str, game_fen: str, is_opponent_turn: bool
    ) -> None:
        if self.has_connection(username):
            self.__connections[username].game_data.game_fen = game_fen
            self.__connections[username].game_data.is_opponent_turn = is_opponent_turn

    def clear_game_data(self, username: str) -> None:
        if self.has_connection(username):
            # TODO сюда можно писать не пустую гейм дату, а нан просто
            is_user_available = self.__connections[username].game_data.is_user_available
            self.__connections[username].game_data = GameData()
            self.__connections[username].game_data.is_user_available = is_user_available

    def has_any_connection(self) -> bool:
        return self.__connections != {}

    def has_not_completed_game(self, opponent_username: str) -> bool:
        return self.get_not_completed_game_data(opponent_username) is not None

    def is_user_available(self, username: str) -> bool:
        return self.has_connection(username) and self.__connections[username].game_data.user_available

    def set_user_available(self, user_available: bool, username: str) -> None:
        if self.has_connection(username):
            self.__connections[username].game_data.user_available = user_available

    def get_not_completed_game_data(self, opponent_username: str) -> GameData | None:
        # TODO так записи в словаре будут оч быстро накапливаться и надо будет их через время удалять
        if self.is_user_available(opponent_username) and self.__connections[opponent_username].game_data.game_fen is not None:
            return self.__connections[opponent_username].game_data

        for username in self.__connections.keys():
            if (
                    self.__connections[username].game_data.opponent_username == opponent_username
                    and self.__connections[username].game_data.game_fen is not None
            ):
                return self.__connections[username].game_data
        return None

    def has_connection(self, username: str | None) -> bool:
        return username is not None and username in self.__connections.keys()
