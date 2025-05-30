from enum import Enum


class ReceivedDataType(Enum):
    TURN_DATA = (0,)
    GAME_END_DATA = (1,)
    GAME_START_DATA = (2,)
    GAME_DECLINE_DATA = (3,)
    UNEXPECTED_DATA = (4,)
