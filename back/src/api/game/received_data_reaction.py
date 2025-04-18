from .web_socket_manager import WebSocketManager
from .schemas.websocket_received_messages_schemas import TurnReceivedMessage, GameEndReceivedMessage, \
    GameStartReceivedMessage
from .schemas.websocket_messages_schemas import TurnMessage, GameEndMessage, GameStartMessage
import datetime

def __convert_move_to_opponent_turn(turn):
    # TODO
    pass


async def on_turn(data: TurnReceivedMessage, manager: WebSocketManager) -> None:
    message = TurnMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        turn=data.turn,
        game_fen=data.game_fen,
    )
    await manager.send_to_user(message.username, message)

    manager.update_game_data_on_turn(data.username, data.opponent_username, data.game_fen)


async def on_game_end(data: GameEndReceivedMessage, manager: WebSocketManager) -> None:
    message = GameEndMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        game_end=data.game_end,
    )
    await manager.send_to_user(message.username, message)
    await manager.send_to_user(message.opponent_username, message)

    manager.clear_game_data(data.username)
    manager.clear_game_data(data.opponent_username)


async def on_game_start(data: GameStartReceivedMessage, manager: WebSocketManager) -> None:
    message = GameStartMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        main_color=data.main_color,
    )
    await manager.send_to_user(message.username, message)

    print(f"{datetime.datetime.now()} before update {data}")
    manager.update_game_data_on_start_game(data.username, data.opponent_username, data.main_color)
    print(f"{datetime.datetime.now()} after update {data}")
