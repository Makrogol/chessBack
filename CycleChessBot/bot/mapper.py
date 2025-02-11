from dataclasses import dataclass
from enum import Enum
from typing import Tuple
import numpy as np

from chess_game.piece_type import PieceType
from chess_game.position import Position

# TODO унести это куда-то в другое место и мб сделать так эту штуку как-то по-другому (хотя придется модель переучивать)
def make_offset(position: Position, i: int, j: int) -> Position:
    return Position((position.i + i + 8) % 8, (position.j + j + 8) % 8)

# +1 +1
def is_south_west_over_board(position_first: Position, position_second: Position) -> bool:
    return position_second == make_offset(position_first, 1, 1)

# +1 -1
def is_south_east_over_board(position_first: Position, position_second: Position) -> bool:
    return position_second == make_offset(position_first, 1, -1)

# -1 +1
def is_north_west_over_board(position_first: Position, position_second: Position) -> bool:
    return position_second == make_offset(position_first, -1, 1)

# -1 -1
def is_north_east_over_board(position_first: Position, position_second: Position) -> bool:
    return position_second == make_offset(position_first, -1, -1)

class QueenDirection(Enum):
    # eight directions
    NORTHWEST = 0
    NORTH = 1
    NORTHEAST = 2
    EAST = 3
    SOUTHEAST = 4
    SOUTH = 5
    SOUTHWEST = 6
    WEST = 7


class KnightMove(Enum):
    # eight possible knight moves
    NORTH_LEFT = 0  # diff == -15  -2 +1
    NORTH_RIGHT = 1  # diff == -17 -2 -1
    EAST_UP = 2  # diff == -6      -1 +2
    EAST_DOWN = 3  # diff == 10    +1 +2
    SOUTH_RIGHT = 4  # diff == 15  +2 -1
    SOUTH_LEFT = 5  # diff == 17   +2 +1
    WEST_DOWN = 6  # diff == 6     +1 -2
    WEST_UP = 7  # diff == -10     -1 -2

def get_knigh_over_board_move(position_first: Position, position_second: Position) -> KnightMove:
    if make_offset(position_first, -2, 1) == position_second:
        return KnightMove.NORTH_LEFT
    if make_offset(position_first, -2, -1) == position_second:
        return KnightMove.NORTH_RIGHT
    if make_offset(position_first, -1, 2) == position_second:
        return KnightMove.EAST_UP
    if make_offset(position_first, 1, 2) == position_second:
        return KnightMove.EAST_DOWN
    if make_offset(position_first, 2, -1) == position_second:
        return KnightMove.SOUTH_RIGHT
    if make_offset(position_first, 2, 1) == position_second:
        return KnightMove.SOUTH_LEFT
    if make_offset(position_first, 1, -2) == position_second:
        return KnightMove.WEST_DOWN
    if make_offset(position_first, -1, -2) == position_second:
        return KnightMove.WEST_UP
    raise Exception(f"Invalid knight over board move, position_first={position_first}, position_second={position_second}")

class UnderPromotion(Enum):
    ROOK = 1
    BISHOP = 2
    KNIGHT = 4

class Mapping:
    """
    The mapper is a dictionary of moves.

    * the index is the type of move
    * the value is the plane's index, or an array of plane indices (for distance)
    """
    # knight moves from north_left to west_up (clockwise)
    knight_mappings = [-15, -17, -6, 10, 15, 17, 6, -10]

    def get_index(self, piece_type: PieceType, direction: Enum, distance: int = 1) -> int:
        if piece_type == PieceType.KNIGHT:
            return 56 + KnightMove(direction).value
        else:
            return QueenDirection(direction) * 8 + distance

    @staticmethod
    def get_underpromotion_move(piece_type: PieceType, position_first: Position, position_second: Position) -> Tuple[UnderPromotion, int]:
        piece_type = UnderPromotion(piece_type.value - 2)
        from_square = position_first.i * 8 + position_first.j
        to_square = position_second.i * 8 + position_second.j
        diff = from_square - to_square
        if to_square < 8:
            # black promotes (1st rank)
            direction = diff - 8
        elif to_square > 55:
            # white promotes (8th rank)
            direction = diff + 8
        return (piece_type, direction)

    @staticmethod
    def get_knight_move(position_first: Position, position_second: Position) -> KnightMove:
        return get_knigh_over_board_move(position_first, position_second)
        # from_square = position_first.i * 8 + position_first.j
        # to_square = position_second.i * 8 + position_second.j
        # diff = from_square - to_square
        # return KnightMove(Mapping.knight_mappings.index(from_square - to_square))

    @staticmethod
    def get_queenlike_move(position_first: Position, position_second: Position) -> Tuple[QueenDirection, int]:
        from_square = position_first.i * 8 + position_first.j
        to_square = position_second.i * 8 + position_second.j
        diff = from_square - to_square
        if diff % 8 == 0:
            # north and south
            if diff > 0:
                direction = QueenDirection.SOUTH
            else:
                direction = QueenDirection.NORTH
            distance = int(diff / 8)
        elif diff % 9 == 0:
            # southwest and northeast
            if diff > 0:
                direction = QueenDirection.SOUTHWEST
            else:
                direction = QueenDirection.NORTHEAST
            distance = np.abs(int(diff / 8))
        elif from_square // 8 == to_square // 8:
            # east and west
            if diff > 0:
                direction = QueenDirection.WEST
            else:
                direction = QueenDirection.EAST
            distance = np.abs(diff)
        elif diff % 7 == 0:
            if diff > 0:
                direction = QueenDirection.SOUTHEAST
            else:
                direction = QueenDirection.NORTHWEST
            distance = np.abs(int(diff / 8)) + 1
        elif is_south_east_over_board(position_first, position_second):
            return QueenDirection.SOUTHEAST
        elif is_south_west_over_board(position_first, position_second):
            return QueenDirection.SOUTHWEST
        elif is_north_east_over_board(position_first, position_second):
            return QueenDirection.NORTHEAST
        elif is_north_west_over_board(position_first, position_second):
            return QueenDirection.NORTHWEST
        else:
            raise Exception(f"Invalid queen-like move, position_first={position_first}, position_second={position_second}")
        return (direction, distance)

    mapper = {
        # queens
        QueenDirection.NORTHWEST: [0, 1, 2, 3, 4, 5, 6],
        QueenDirection.NORTH: [7, 8, 9, 10, 11, 12, 13],
        QueenDirection.NORTHEAST: [14, 15, 16, 17, 18, 19, 20],
        QueenDirection.EAST: [21, 22, 23, 24, 25, 26, 27],
        QueenDirection.SOUTHEAST: [28, 29, 30, 31, 32, 33, 34],
        QueenDirection.SOUTH: [35, 36, 37, 38, 39, 40, 41],
        QueenDirection.SOUTHWEST: [42, 43, 44, 45, 46, 47, 48],
        QueenDirection.WEST: [49, 50, 51, 52, 53, 54, 55],
        # knights
        KnightMove.NORTH_LEFT: 56,
        KnightMove.NORTH_RIGHT: 57,
        KnightMove.EAST_UP: 58,
        KnightMove.EAST_DOWN: 59,
        KnightMove.SOUTH_RIGHT: 60,
        KnightMove.SOUTH_LEFT: 61,
        KnightMove.WEST_DOWN: 62,
        KnightMove.WEST_UP: 63,
        # underpromotions
        UnderPromotion.KNIGHT: [64, 65, 66],
        UnderPromotion.BISHOP: [67, 68, 69],
        UnderPromotion.ROOK: [70, 71, 72]
    }