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