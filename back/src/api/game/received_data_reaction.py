from .web_socket_manager import WebSocketManager
from .schemas.websocket_received_messages_schemas import TurnReceivedMessage, GameEndReceivedMessage, GameStartReceivedMessage
from .schemas.websocket_messages_schemas import TurnMessage, GameEndMessage, GameStartMessage


def __convert_turn_to_opponent_turn(turn):
    # TODO
    pass


async def on_turn(data: TurnReceivedMessage, manager: WebSocketManager) -> None:
    message = TurnMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        turn=data.turn,
    )
    await manager.send_to_user(message.username, message)


async def on_game_end(data: GameEndReceivedMessage, manager: WebSocketManager) -> None:
    message = GameEndMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        game_end=data.game_end,
    )
    await manager.send_to_user(message.username, message)
    await manager.send_to_user(message.opponent_username, message)


async def on_game_start(data: GameStartReceivedMessage, manager: WebSocketManager) -> None:
    message = GameStartMessage(
        username=data.opponent_username,
        opponent_username=data.username,
    )
    await manager.send_to_user(message.username, message)
