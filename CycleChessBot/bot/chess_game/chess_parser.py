
from chess_game.color import Color
from chess_game.position import Position
from chess_game.piece_type import PieceType
from chess_game.move import Move

class ChessParser():
    
    def color(self, color: Color) -> bytearray:
        return ("color:{}".format(color)).encode('utf-8')

    def positionsToMove(self, position_first: Position, position_second: Position) -> bytearray:
        return ("positionsToMove:{};{}".format(position_first, position_second)).encode('utf-8')

    def positionAndPieceTypeForMagicPawnTransformation(self, position: Position, piece_type: PieceType) -> bytearray:
        return ("positionAndPieceType:{};{}".format(position, piece_type)).encode('utf-8')
    
    def position(self, position: Position) -> bytearray:
        return str(position).encode('utf-8')
    
    def move(self, move: Move) -> bytearray:
        return str(move).encode('utf-8')

    def fen(self, fen: str) -> bytearray:
        return fen.encode('utf-8')
