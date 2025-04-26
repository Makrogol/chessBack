from .web_socket_manager import WebSocketManager
from .schemas.websocket_received_messages_schemas import (
    TurnReceivedMessage,
    GameEndReceivedMessage,
    GameStartReceivedMessage,
    GameDeclineReceivedMessage,
)
from .schemas.websocket_sent_messages_schemas import (
    TurnSentMessage,
    GameEndSentMessage,
    GameStartSentMessage,
    GameDeclineSentMessage,
)
import datetime

from ...core.config import BASE_DIR
from ...cpp_bridge.chess_unparser import ChessUnparserByte
from ...cpp_bridge.chess_parser import ChessParserStr
from ...bot.bridge import Bridge
from ...cpp_bridge.color import get_another_color

unparser = ChessUnparserByte()
parser = ChessParserStr()
bridge = Bridge(str(BASE_DIR / "src" / "bot" / "models" / "model.keras"))


def change_move_for_opponent(move_str: str) -> str:
    move = unparser.move(move_str)
    move.change_for_opponent()
    return parser.move(move)


async def on_turn(data: TurnReceivedMessage, manager: WebSocketManager) -> None:
    print(f"on turn username {data.username}")
    if manager.is_user_play_with_bot(data.username):
        print(f'user {data.username} is playing with bot')
        move, fen = bridge.predict_move(data.game_fen)
        print(f'turn from bot {parser.move(move)}, new game fen: {fen}')
        message = TurnSentMessage(
            username=data.username,
            opponent_username=data.opponent_username,
            turn=parser.move(move),
        )
        await manager.send_to_user(message.username, message)

        manager.update_game_data_on_turn(data.username, fen, False)
        return

    message = TurnSentMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        turn=change_move_for_opponent(data.turn),
    )
    await manager.send_to_user(message.username, message)

    manager.update_game_data_on_turn(data.username, data.game_fen, True)
    manager.update_game_data_on_turn(data.opponent_username, data.game_fen, False)


async def on_game_end(data: GameEndReceivedMessage, manager: WebSocketManager) -> None:
    message = GameEndSentMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        game_end=data.game_end,
    )
    await manager.send_to_user(message.username, message)
    await manager.send_to_user(message.opponent_username, message)

    manager.clear_game_data(data.username)
    manager.clear_game_data(data.opponent_username)


async def on_game_start(data: GameStartReceivedMessage, manager: WebSocketManager) -> None:
    if manager.is_user_in_game(data.username):
        message = GameDeclineSentMessage(
            username=data.opponent_username,
            opponent_username=data.username,
            decline_reason=f"Пользователь {data.username} уже в игре",
        )
        await manager.send_to_user(message.username, message)
        return

    message = GameStartSentMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        main_color=data.main_color,
        is_switched_color=data.is_switched_color,
    )
    await manager.send_to_user(message.username, message)

    print(f"{datetime.datetime.now()} before update {data}")
    manager.update_game_data_on_start_game(
        username=data.username,
        opponent_username=data.opponent_username,
        main_color=data.main_color,
        is_switched_color=data.is_switched_color,
        is_play_with_bot=bool(data.is_play_with_bot)
    )
    if not data.is_play_with_bot:
        manager.update_game_data_on_start_game(
            username=data.opponent_username,
            opponent_username=data.username,
            main_color=parser.color(get_another_color(unparser.color_str(data.main_color))),
            is_switched_color=data.is_switched_color,
            is_play_with_bot=bool(data.is_play_with_bot)
        )
    print(f"{datetime.datetime.now()} after update {data}")


async def on_game_decline(data: GameDeclineReceivedMessage, manager: WebSocketManager) -> None:
    message = GameDeclineSentMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        decline_reason=data.decline_reason,
    )
    await manager.send_to_user(message.username, message)

    manager.clear_game_data(data.username)
    manager.clear_game_data(data.opponent_username)
