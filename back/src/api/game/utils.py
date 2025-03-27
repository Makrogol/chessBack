from .received_data_reaction import on_turn, on_game_end, on_game_start
from .shemas import TurnReceivedMessage, GameEndReceivedMessage, GameStartReceivedMessage
from ...core.utils import has_field
from .recieved_data_type import ReceivedDataType
from .web_socket_manager import WebSocketManager


def parse_received_data(data: dict) -> ReceivedDataType:
    if not has_field(data, "username") or not has_field(data, "opponent_username"):
        return ReceivedDataType.UNEXPECTED_DATA
    if has_field(data, "turn"):
        return ReceivedDataType.TURN_DATA
    if has_field(data, "game_end"):
        return ReceivedDataType.GAME_END_DATA
    return ReceivedDataType.GAME_START_DATA


async def data_reaction(manager: WebSocketManager, data: dict):
    received_data_type = parse_received_data(data)
    print("data ", data)
    print("received_data_type ", received_data_type)
    match received_data_type:
        case ReceivedDataType.TURN_DATA: # Сама игра
            print('turn ', data)
            await on_turn(TurnReceivedMessage(**data), manager)
        case ReceivedDataType.GAME_END_DATA: # Завершение игры
            print('end game ', data)
            await on_game_end(GameEndReceivedMessage(**data), manager)
        case ReceivedDataType.GAME_START_DATA: # Вызов на игру
            print('start game ', data)
            await on_game_start(GameStartReceivedMessage(**data), manager)
