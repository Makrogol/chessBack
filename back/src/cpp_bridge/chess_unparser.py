from .move import Move
from .position import Position
from .game_state import GameState
from .move_type import MoveType
from .piece_type import PieceType
from .color import Color
from .board import PieceTypeAndColor, BoardRepresentation


class ChessUnparserStr:
    # TODO implement
    pass


class ChessUnparserByte:

    def position(self, position_str: bytearray) -> Position:
        i, j = map(int, position_str.decode("utf-8").split(","))
        return Position(i, j)

    def position(self, position_str: str) -> Position:
        i, j = map(int, position_str.split(","))
        return Position(i, j)

    def move(self, move_str: str) -> Move:
        move = move_str.split(".")
        return Move(
            self.position(move[0]),
            self.position(move[1]),
            PieceType(int(move[2])) if len(move) > 2 else None,
        )

    def color(self, color_str: bytearray) -> Color:
        color_int = int(color_str.decode("utf-8"))
        return Color(color_int)

    def color_str(self, color_str: str) -> Color:
        color_int = int(color_str)
        return Color(color_int)

    def possible_moves(self, possible_moves_str: bytearray) -> list[Move]:
        possible_moves = possible_moves_str.decode("utf-8").split(":")[1].split(";")
        # Это значит, что мувов не пришло из либы
        if possible_moves == [""]:
            return []
        return [self.move(move) for move in possible_moves]

    def possible_positions(self, possible_positions_str: bytearray) -> list[Position]:
        possible_positions = (
            possible_positions_str.decode("utf-8").split(":")[1].split(";")
        )
        return [self.position(move) for move in possible_positions]

    def game_state(self, game_state_str: bytearray) -> GameState:
        game_state_int = int(game_state_str.decode("utf-8").split(":")[1])
        return GameState(game_state_int)

    def result_do_magic_pawn_transformation(self, result: bytearray) -> bool:
        return bool(int(result.decode("utf-8").split(":")[1]))

    def result_can_do_one_move_and_draw_by_fifty_moves(self, result: bytearray) -> bool:
        return bool(int(result.decode("utf-8").split(":")[1]))

    def result_can_do_passant(self, result: bytearray) -> bool:
        return bool(int(result.decode("utf-8").split(":")[1]))

    def move_type(self, move_type_str: bytearray) -> MoveType:
        move_type_int = int(move_type_str.decode("utf-8").split(":")[1])
        return MoveType(move_type_int)

    def board_representation(
            self, board_representation_str: bytearray
    ) -> BoardRepresentation:
        repr = board_representation_str.decode("utf-8").split(":")[1].split(";")
        board_repr = []
        for i in range(8):
            board_repr.append([])
            for j in range(8):
                piece_type_int, color_int = map(int, repr[i][j].split(","))
                board_repr[i].append(
                    PieceTypeAndColor(PieceType(piece_type_int), Color(color_int))
                )
        return board_repr
