#pragma once

// TODO мб перенести это в папку game

enum GameState {
    ON_GOING = 0,
    MATE_FOR_WHITE = 1,
    MATE_FOR_BLACK = 2,
    PATE = 3,
    DRAW = 4,
    CHECK_FOR_WHITE = 5,
    CHECK_FOR_BLACK = 6,
};
