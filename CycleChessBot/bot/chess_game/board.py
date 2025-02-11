
from chess_game.cell import Cell
from chess_game.piece import Piece
from chess_game.color import Color, get_another_color
from chess_game.piece_type import PieceType
from chess_game.position import Position
from chess_game.piece_type_and_color import PieceTypeAndColor

type BoardRepresentation = list[list[PieceTypeAndColor]]

def get_type_from_fen(fen: str) -> PieceType:
    if fen == 'b' or fen == 'B':
        return PieceType.BISHOP
    if fen == 'k' or fen == 'K':
        return PieceType.KING
    if fen == 'q' or fen == 'Q':
        return PieceType.QUEEN
    if fen == 'r' or fen == 'R':
        return PieceType.ROOK
    if fen == 'n' or fen == 'N':
        return PieceType.KNIGHT
    if fen == 'p' or fen == 'P':
        return PieceType.PAWN
    return PieceType.EMPTY

def get_piece_type_and_color_from_fen(fen: str) -> PieceTypeAndColor:
    type = get_type_from_fen(fen)
    if fen.isupper():
        return PieceTypeAndColor(type, Color.WHITE)
    return PieceTypeAndColor(type, Color.BLACK)


class Board():
    def __init__(self) -> None:
        self.field = []
        self.turn_color = Color.WHITE
        self.passant_position = None
        # Сначала королевская и ферзевая для белых, потом тоже самое для черных
        # TODO исправить на snake_case
        self.castling_rights = []
        self.countMovesWithoutEatingOrPawnsMove = 0
        self.countMoves = 0
        self.fen = ""

    def create_empty_field(self) -> None:
        self.field.clear()
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

    # def create_from_representation(self, representation: BoardRepresentation) -> None:
    #     self.create_empty_field()
    #     for i in range(8):
    #         for j in range(8):
    #             with representation[i][j] as piece:
    #                 self.field[i][j].set_piece(Piece(piece.piece_type, piece.color))
    
    def get_fen(self) -> str:
        return self.fen
    
    def create_from_fen(self, fen: str) -> None:
        # TODO описать вообще все тут более защищенно, а не так, чтобы на соплях держалось
        self.fen = fen
        self.create_empty_field()
        fen_elements = fen.split(' ')
        if len(fen_elements) < 5:
            # TODO error need log
            return
        iter = 0
        board_str = fen_elements[iter].split('/')
        iter += 1

        if len(board_str) != 8:
            # TODO error need log
            return
        
        self.turn_color = Color.WHITE if fen_elements[iter] == 'w' else Color.BLACK
        iter += 1

        if len(fen_elements) == 6:
            self.castling_rights = ['K' in fen_elements[iter], 'Q' in fen_elements[iter], 'k' in fen_elements[iter], 'q' in fen_elements[iter]]
            iter += 1
        else:
            self.castling_rights = [False, False, False, False]

        self.passant_position = Position(int(fen_elements[iter][0]), int(fen_elements[iter][2])) if fen_elements[iter] != '-' else None
        iter += 1

        self.countMovesWithoutEatingOrPawnsMove = int(fen_elements[iter])
        iter += 1

        self.countMoves = int(fen_elements[iter]) 
        iter += 1

        for i in range(8):
            j = 0
            k = 0
            while k < len(board_str[i]):
                if board_str[i][k].isdigit():
                    j += int(board_str[i][k])
                elif board_str[i][k].isalpha():
                    type_and_color = get_piece_type_and_color_from_fen(board_str[i][k])
                    count_steps = int(board_str[i][k + 1])
                    piece = Piece(type_and_color.piece_type, type_and_color.color, Position(i, j))
                    piece.count_steps = count_steps
                    self.field[i][j].set_piece(piece)
                    j += 1
                    k += 1
                k += 1
    
    def get_all_pieces(self):
        pieces = []
        for line in self.field:
            for cell in line:
                if cell.has_piece():
                    pieces.append(cell.piece)
        return pieces

    def has_piece(self, position: Position) -> bool:
        return self.field[position.i][position.j].has_piece()

    def get_piece(self, position: Position) -> Piece | None:
        return self.field[position.i][position.j].piece

    def get_pieces_by_type_and_color(self, color: Color, piece_type: PieceType) -> list[Piece]:
        pieces = []
        for line in self.field:
            for cell in line:
                if cell.has_piece():
                    if cell.piece.color == color and cell.piece.piece_type == piece_type:
                        pieces.append(cell.piece)
        return pieces
    
    def move_piece(self, position_first: Position, position_second: Position) -> bool:
        self.field[position_second.i][position_second.j].set_piece(self.field[position_first.i][position_first.j].piece)
        self.field[position_first.i][position_first.j].set_piece(None)
