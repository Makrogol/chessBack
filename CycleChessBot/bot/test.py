from chess_game.cpp_api import CppApi
from chess_game.position import Position
from chess_game.color import Color

from GUI.display import GUI

# TODO написать тест на фен, когда нет прав рокироваться
# cpp_api = CppApi()
# cpp_api.startGameWithFen(Color.WHITE, 'r0n0b0q01b0n0r0/p0p0p0k1p0p0p01/7p1/3p14/8/N13P12P1/P0P0P0P0K1P0P01/R01B0Q01B0N0R0 b - 4 2')
# cpp_api.allPossibleMoves()

cpp_api = CppApi()
cpp_api.startGameWithFen(Color.WHITE, 'r01b0k1n4r12/p0p0p02p01P5/8/2P21p13/2p23P1n4/P13P12N1/1B1P0P02K3P0/R0N01Q02R11 w - 2 2')
moves = cpp_api.allPossibleMoves()
for move in moves:
    print(move)

gui = GUI(400, 400, True, 'r01b0k1n4r12/p0p0p02p01P5/8/2P21p13/2p23P1n4/P13P12N1/1B1P0P02K3P0/R0N01Q02R11 w - 2 2')
gui.start()
while True:
    gui.draw()

