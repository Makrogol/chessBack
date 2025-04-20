"""
    Ahira Justice, ADEFOKUN
    justiceahira@gmail.com
"""

import os
import sys
import pygame
from pygame.locals import *

import pygamepopup
from pygamepopup.menu_manager import MenuManager, InfoBox
from pygamepopup.components import Button

from .board import Board

from chess_game.color import Color
from chess_game.move import Move
from chess_game.piece import Piece
from chess_game.piece_type import PieceType

os.environ["SDL_VIDEO_CENTERED"] = "1"  # Centre display window.

FPS = 30
FPSCLOCK = pygame.time.Clock()

colors = {
    "Background": (75, 75, 75),
    "White": (255, 255, 255),
    "Black": (0, 0, 0),
}

BGCOLOR = colors["Background"]

BASICFONTSIZE = 30


class GUI:
    def __init__(self, width: int, height: int, color_is_white: bool, fen: str):
        self.gameboard: Board = None
        self.WINDOWWIDTH = width
        self.WINDOWHEIGHT = height
        self.color_is_white = color_is_white
        self.move = None

        self.fen = fen

        # for moving pieces
        self.position_first = None
        self.position_second = None
        # for promotion
        self.promoting = False

        self.start()

    def start(self) -> None:
        pygame.init()
        pygame.display.set_caption("Chess with Reinforcement Learning")
        pygamepopup.init()

        # Setting up the GUI window.
        self.DISPLAYSURF = pygame.display.set_mode(
            (self.WINDOWWIDTH, self.WINDOWHEIGHT)
        )

        # set up popup manager
        self.menu_manager = MenuManager(screen=self.DISPLAYSURF)

        # BASICFONT = pygame.font.SysFont('roboto', BASICFONTSIZE)

        self.checkForQuit()

        self.DISPLAYSURF.fill(BGCOLOR)
        self.gameboard = Board(
            colors,
            BGCOLOR,
            self.DISPLAYSURF,
            self.WINDOWWIDTH,
            self.WINDOWHEIGHT,
            self.fen,
        )
        self.gameboard.displayBoard()

        self.promotion_menu = InfoBox(
            "Choose a piece to promote to:",
            [
                [
                    Button(
                        title="Queen",
                        callback=lambda: self.promote(PieceType.QUEEN),
                        size=(100, 50),
                    ),
                ],
                [
                    Button(
                        title="Rook",
                        callback=lambda: self.promote(PieceType.ROOK),
                        size=(100, 50),
                    ),
                ],
                [
                    Button(
                        title="Bishop",
                        callback=lambda: self.promote(PieceType.BISHOP),
                        size=(100, 50),
                    ),
                ],
                [
                    Button(
                        title="Knight",
                        callback=lambda: self.promote(PieceType.KNIGHT),
                        size=(100, 50),
                    ),
                ],
            ],
            has_close_button=False,
        )

        self.draw()

    def promote(self, piece: PieceType) -> None:
        self.move_piece(piece)

    def show_promotion_menu(self) -> None:
        self.promoting = True
        self.menu_manager.open_menu(self.promotion_menu)

    def make_move(self, move: Move):
        self.gameboard.cpp_api.tryDoMove(move)

    def draw(self):
        self.gameboard.displayBoard()
        self.gameboard.updatePieces()

        if self.promoting:
            self.menu_manager.display()
        pygame.display.update()
        FPSCLOCK.tick(FPS)
        self.get_events()
        # update selected tile
        self.gameboard.selected_position = self.position_first

    def get_events(self):
        self.checkForQuit()
        self.get_click_events()

    def get_click_events(self) -> None:
        for event in pygame.event.get():
            if event.type == MOUSEBUTTONUP:
                if self.promoting:
                    self.menu_manager.click(event.button, event.pos)
                    continue
                x, y = pygame.mouse.get_pos()
                if event.button == 3:
                    # right mouse button => clear
                    self.position_first = None
                    self.position_second = None
                elif self.position_first is None:
                    # fist click: get position_first
                    self.position_first = self.gameboard.get_square_on_pos(x, y)
                elif self.gameboard.get_square_on_pos(x, y) != self.position_first:
                    # from and position_second are different, try to move piece
                    self.position_second = self.gameboard.get_square_on_pos(x, y)
                    piece: Piece = self.gameboard.get_piece_to_move(
                        self.position_first, self.position_second
                    )
                    if piece is None:
                        print("Piece is none")
                        self.position_first = None
                        self.position_second = None
                        continue
                    if piece.color != self.gameboard.cpp_api.getCurrentTurn():
                        print("Wrong color")
                        self.position_first = None
                        self.position_second = None
                        continue

                    if piece.piece_type == PieceType.PAWN and (
                        (piece.color == Color.WHITE and self.position_second.i == 7)
                        or (piece.color == Color.BLACK and self.position_second.i == 0)
                    ):
                        # get promotion from menu
                        self.promotion_choice: PieceType = None
                        self.show_promotion_menu()
                    else:
                        self.move_piece(piece.piece_type)

    def move_piece(self, piece: PieceType) -> None:
        # move piece to to_square
        # create san from move
        try:
            move = Move(self.position_first, self.position_second)
            if self.promoting:
                move.promotion = piece
                self.promoting = False
            self.gameboard.cpp_api.tryDoMove(move)
            self.move = move
        except ValueError as e:
            print("Invalid move")
            raise (e)
        self.position_first = None
        self.position_second = None

    def terminate(self):
        pygame.quit()
        sys.exit()

    def checkForQuit(self):
        for event in pygame.event.get(QUIT):  # get all the QUIT events
            self.terminate()  # terminate if any QUIT events are present
        for event in pygame.event.get(KEYUP):  # get all the KEYUP events
            if event.key == K_ESCAPE:
                self.terminate()  # terminate if the KEYUP event was for the Esc key
            pygame.event.post(event)  # put the other KEYUP event objects back

        return False
