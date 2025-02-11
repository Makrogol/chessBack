from chess_game.cpp_api import CppApi
from chess_game.position import Position
from chess_game.color import Color

from GUI.display import GUI

# TODO написать тест на фен, когда нет прав рокироваться
# cpp_api = CppApi()
# cpp_api.startGameWithFen(Color.WHITE, 'r0n0b0q01b0n0r0/p0p0p0k1p0p0p01/7p1/3p14/8/N13P12P1/P0P0P0P0K1P0P01/R01B0Q01B0N0R0 b - 4 2')
# cpp_api.allPossibleMoves()

gui = GUI(400, 400, True, 'r0k33b0n0r0/4p0N51p0/b1p16/p11p12q4P21/Q21P1p3P1p22/3P11n31P1/P0P06/R21B0K11B01R0 b - 4 2')
gui.start()
while True:
    gui.draw()

# possible_moves = "asd:".split(':')[1].split(';')
# print(possible_moves == [""])