
from game.cell import Cell
from game.piece import Piece
from game.color import Color, get_another_color
from game.piece_type import PieceType
from game.position import Position

type BoardRepresentation = list[list[PieceTypeAndColor]]

class PieceTypeAndColor():
    def __init__(self, piece_type: PieceType, color: Color) -> None:
        self.piece_type = piece_type
        self.color = color

class Board():
    def __init__(self) -> None:
        self.field = []

    def create_empty_field(self) -> None:
        self.field.append([])
        self.field[0].append(Cell(Color.WHITE, Position(0, 0)))

        for i in range(1, 8):
            color = get_another_color(self.field[0][i - 1].color)
            self.field[0].append(Cell(color, Position(0, i)))

        for i in range(1, 8):
            color = get_another_color(self.field[i - 1][0].color)
            self.field.append([])
            for j in range(8):
                self.field[i].append(Cell(color, Position(i, j)))
                color = get_another_color(color)

    
    def create_from_representation(self, representation: BoardRepresentation) -> None:
        self.create_empty_field()
        for i in range(8):
            for j in range(8):
                with representation[i][j] as piece:
                    self.field[i][j].set_piece(Piece(piece.piece_type, piece.color))
    
    def move_piece(self, position_first: Position, position_second: Position) -> bool:
        self.field[position_second.i][position_second.j].set_piece(self.field[position_first.i][position_first.j].piece)
        self.field[position_first.i][position_first.j].set_piece(None)


        