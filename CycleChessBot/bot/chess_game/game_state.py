from enum import Enum

class GameState(Enum):
    ON_GOING = 0
    MATE_FOR_WHITE = 1
    MATE_FOR_BLACK = 2
    PATE = 3
    DRAW = 4
    CHECK_FOR_WHITE = 5
    CHECK_FOR_BLACK = 6

    # TODO сделать свой класс енум (отнаследовать от дефолтного и перегрузить превращение в строку)
    def __str__(self) -> str:
        return str(self.value)

def is_game_over(game_state: GameState) -> bool:
    return game_state == GameState.MATE_FOR_BLACK or\
            game_state == GameState.MATE_FOR_WHITE or\
            game_state == GameState.PATE or\
            game_state == GameState.DRAW