"""
    Ahira Justice, ADEFOKUN
    justiceahira@gmail.com
"""


import os
from typing import Tuple
import pygame
from pygame.locals import *

from pygame.surface import Surface

from .pieces import PieceImage

from chess_game.piece import Piece
from chess_game.position import Position
from chess_game.board import Board as ChessBoard
from chess_game.color import Color
from chess_game.piece_type import PieceType
from chess_game.cpp_api import CppApi


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
IMAGE_DIR = os.path.join(BASE_DIR, 'images')


class Board:
    btile = pygame.image.load(os.path.join(IMAGE_DIR, 'btile.png'))
    wtile = pygame.image.load(os.path.join(IMAGE_DIR, 'wtile.png'))
    tile_selected = pygame.image.load(os.path.join(IMAGE_DIR, 'tile-selected.png'))

    def __init__(self, colors: dict, BGCOLOR: tuple, DISPLAYSURF: Surface, width: int, height: int, fen: str):
        self.colors = colors
        self.BGCOLOR = BGCOLOR
        self.DISPLAYSURF = DISPLAYSURF

        self.WINDOWWIDTH = width
        self.WINDOWHEIGHT = height

        self.pieceRect = []

        # create boardRect procedurally, based on the width and height of the screen
        square_size = min(self.WINDOWWIDTH, self.WINDOWHEIGHT) / 8

        squares = []
        for y in range(8):
            squares.append([])
            for x in range(8):
                squares[-1].append((x*square_size, y*square_size))

        Board.boardRect = squares

        self.square_size = square_size

        # change size of wtile and btile
        Board.wtile = pygame.transform.scale(
            Board.wtile, (int(self.square_size), int(self.square_size)))
        Board.btile = pygame.transform.scale(
            Board.btile, (int(self.square_size), int(self.square_size)))
        Board.tile_selected = pygame.transform.scale(
            Board.tile_selected, (int(self.square_size), int(self.square_size)))
        # keep the currently selected square
        self.selected_position = None


        self.cpp_api = CppApi()
        if self.cpp_api.getFen() != fen:
            self.cpp_api.startGameWithFen(Color.WHITE, fen)

    def get_square_on_pos(self, x, y) -> Position:
        # calculate the square based on mouse position
        return Position(int(x / self.square_size), int(y / self.square_size))

    def get_piece_to_move(self, position: Position) -> Piece:
        # get piece from position
        board = ChessBoard()
        board.create_from_fen(self.cpp_api.getFen())
        return board.get_piece(position)
        

    def displayBoard(self) -> None:
        """
        Displays the board tiles on the screen
        """
        # fill the background with the background color
        self.DISPLAYSURF.fill(self.BGCOLOR)
        # draw a rectangle around the board
        pygame.draw.rect(
            self.DISPLAYSURF, self.colors['Black'], (0, 0, self.WINDOWWIDTH, self.WINDOWHEIGHT), 10)
        # draw the board tiles
        self.drawTiles()

    def drawTiles(self):
        for i in range(len(Board.boardRect)):
            for j in range(len(Board.boardRect[i])):
                if self.is_selected(i, j):
                    tile = Board.tile_selected
                elif Board.isEven(i) and Board.isEven(j) or not Board.isEven(i) and not Board.isEven(j):
                        tile = Board.wtile
                else:
                        tile = Board.btile
                self.DISPLAYSURF.blit(tile, Board.boardRect[i][j])

    def is_selected(self, i: int, j: int) -> bool:
        """
        Returns True if a tile is selected, else false
        """
        return self.selected_position is not None and self.selected_position.i == i and self.selected_position.j == j

    @staticmethod
    def isEven(n):
        return n % 2 == 0

    def createPiece(self, color: Color, piece_type: PieceType, position: Position):
        piece = PieceImage(color, piece_type,
                           self.DISPLAYSURF, self.square_size)
        # convert square to pixel position
        position = Position(self.square_size * position.i, self.square_size * position.j)
        piece.setPosition(position)
        return piece

    def updatePieces(self):
        # get pieces from fen
        board = ChessBoard()
        board.create_from_fen(self.cpp_api.getFen())
        pieces: list[Piece] = board.get_all_pieces()
        self.pieceRect: list[PieceImage] = []

        for piece in pieces:
            piece_image = self.createPiece(
                piece.color, piece.piece_type, piece.position)
            self.pieceRect.append(piece_image)

        for piece_image in self.pieceRect:
            piece_image.displayPiece()