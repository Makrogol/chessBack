from chess_game.cpp_api import CppApi
from chess_game.position import Position
from chess_game.color import Color

from GUI.display import GUI

# TODO написать тест на фен, когда нет прав рокироваться
# cpp_api = CppApi()
# cpp_api.startGameWithFen(Color.WHITE, 'r0n0b0q01b0n0r0/p0p0p0k1p0p0p01/7p1/3p14/8/N13P12P1/P0P0P0P0K1P0P01/R01B0Q01B0N0R0 b - 4 2')
# cpp_api.allPossibleMoves()

gui = GUI(400, 400, True, 'r0n0b0k1r23/p0p0q2p0p0p0b1p0/2p15/B25Q41/1P11P24/5P12/P01P0N1P01P0P0/R03K0B0N0R0 b KQ - 0 2')
gui.start()
while True:
    gui.draw()
