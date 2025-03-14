from web_socket_manager import WebSocketManager
from shemas import TurnMessage, GameEndMessage, GameStartMessage, TurnReceivedMessage, GameEndReceivedMessage, \
    GameStartReceivedMessage


def __convert_turn_to_opponent_turn(turn):
    # TODO
    pass


async def on_turn(data: TurnReceivedMessage, manager: WebSocketManager) -> None:
    message = TurnMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        turn=data.turn,
    )
    await manager.send_to_user(data.opponent_username, message)


async def on_game_end(data: GameEndReceivedMessage, manager: WebSocketManager) -> None:
    message = GameEndMessage(
        username=data.opponent_username,
        opponent_username=data.username,
        game_end=data.game_end,
    )
    await manager.send_to_user(data.username, message)
    await manager.send_to_user(data.opponent_username, message)

    manager.remove_connection(data.username)
    manager.remove_connection(data.opponent_username)


async def on_game_start(data: GameStartReceivedMessage, manager: WebSocketManager) -> None:
    message = GameStartMessage(
        username=data.opponent_username,
        opponent_username=data.username,
    )
    await manager.send_to_user(data.opponent_username, message)
