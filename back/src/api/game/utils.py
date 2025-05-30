from .received_data_reaction import on_turn, on_game_end, on_game_start, on_game_decline
from .schemas.websocket_sent_messages_schemas import (
    UserAvailableSentMessage,
    NotCompletedGameSentMessage,
)
from .schemas.websocket_received_messages_schemas import (
    TurnReceivedMessage,
    GameEndReceivedMessage,
    GameStartReceivedMessage,
    GameDeclineReceivedMessage,
)
from ...core.utils import has_field
from .recieved_data_type import ReceivedDataType
from .web_socket_manager import WebSocketManager
from ...cpp_bridge.chess_unparser import ChessUnparserByte
from ...cpp_bridge.chess_parser import ChessParserStr
from ...cpp_bridge.color import get_another_color

unparser = ChessUnparserByte()
parser = ChessParserStr()


def parse_received_data(data: dict) -> ReceivedDataType:
    if not has_field(data, "username") or not has_field(data, "opponent_username"):
        return ReceivedDataType.UNEXPECTED_DATA
    if has_field(data, "turn"):
        return ReceivedDataType.TURN_DATA
    if has_field(data, "game_end"):
        return ReceivedDataType.GAME_END_DATA
    if has_field(data, "decline_reason"):
        return ReceivedDataType.GAME_DECLINE_DATA
    return ReceivedDataType.GAME_START_DATA


async def data_reaction(manager: WebSocketManager, data: dict) -> None:
    received_data_type = parse_received_data(data)
    print(f'received data {data}')
    match received_data_type:
        case ReceivedDataType.TURN_DATA:  # Сама игра
            await on_turn(TurnReceivedMessage(**data), manager)
        case ReceivedDataType.GAME_END_DATA:  # Завершение игры
            await on_game_end(GameEndReceivedMessage(**data), manager)
        case ReceivedDataType.GAME_START_DATA:  # Вызов на игру
            await on_game_start(GameStartReceivedMessage(**data), manager)
        case ReceivedDataType.GAME_DECLINE_DATA:
            await on_game_decline(GameDeclineReceivedMessage(**data), manager)


async def send_user_available_state(
        manager: WebSocketManager, username: str, user_available: bool
) -> None:
    message = UserAvailableSentMessage(username=username, user_available=user_available)
    await manager.broadcast(message)

    manager.set_user_available(user_available, username)


async def send_user_has_not_completed_game(
        manager: WebSocketManager, username: str
) -> None:
    game_data = manager.get_not_completed_game_data(username)
    message = NotCompletedGameSentMessage(
        username=game_data.opponent_username,
        opponent_username=game_data.username,
        game_fen=game_data.game_fen,
        is_opponent_turn=game_data.is_opponent_turn,
        main_color=game_data.main_color,
        is_switched_color=game_data.is_switched_color,
        is_play_with_bot=game_data.is_play_with_bot,
    ) if game_data.username != username else NotCompletedGameSentMessage(  # TODO убрать этот костыль галимый
        username=game_data.username,
        opponent_username=game_data.opponent_username,
        game_fen=game_data.game_fen,
        is_opponent_turn=not game_data.is_opponent_turn,
        main_color=parser.color(get_another_color(unparser.color_str(game_data.main_color))),
        is_switched_color=game_data.is_switched_color,
        is_play_with_bot=game_data.is_play_with_bot,
    )

    manager.update_game_data_for_opponent(username, game_data)
    await manager.send_to_user(username, message)
