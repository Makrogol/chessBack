from chess_game.cpp_api import CppApi
from chess_game.color import Color

from GUI.display import GUI

# TODO написать тест на фен, когда нет прав рокироваться
# cpp_api = CppApi()
# cpp_api.startGameWithFen(Color.WHITE, 'r0n0b0q01b0n0r0/p0p0p0k1p0p0p01/7p1/3p14/8/N13P12P1/P0P0P0P0K1P0P01/R01B0Q01B0N0R0 b - 4 2')
# cpp_api.allPossibleMoves()

gui = GUI(400, 400, True, '1r11q0k0b0n01/p0p0p01p0p02/n12p13P3/7p1/8/P11N14b1/1P0P0P0P0K1P0P0/R01B0Q01B01R0 w - 2 2')
gui.start()
while True:
    gui.draw()
