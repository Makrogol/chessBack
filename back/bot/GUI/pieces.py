"""
    Ahira Justice, ADEFOKUN
    justiceahira@gmail.com
"""

import os
import pygame

from chess_game.color import Color
from chess_game.piece_type import PieceType
from chess_game.position import Position

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
IMAGE_DIR = os.path.join(BASE_DIR, "images")


class PieceImage:
    bBishop = pygame.image.load(os.path.join(IMAGE_DIR, "bB.png"))
    bKing = pygame.image.load(os.path.join(IMAGE_DIR, "bK.png"))
    bKnight = pygame.image.load(os.path.join(IMAGE_DIR, "bN.png"))
    bPawn = pygame.image.load(os.path.join(IMAGE_DIR, "bP.png"))
    bQueen = pygame.image.load(os.path.join(IMAGE_DIR, "bQ.png"))
    bRook = pygame.image.load(os.path.join(IMAGE_DIR, "bR.png"))

    wBishop = pygame.image.load(os.path.join(IMAGE_DIR, "wB.png"))
    wKing = pygame.image.load(os.path.join(IMAGE_DIR, "wK.png"))
    wKnight = pygame.image.load(os.path.join(IMAGE_DIR, "wN.png"))
    wPawn = pygame.image.load(os.path.join(IMAGE_DIR, "wP.png"))
    wQueen = pygame.image.load(os.path.join(IMAGE_DIR, "wQ.png"))
    wRook = pygame.image.load(os.path.join(IMAGE_DIR, "wR.png"))

    pieceImages = [
        (wPawn, bPawn),
        (wKing, bKing),
        (wRook, bRook),
        (wBishop, bBishop),
        (wQueen, bQueen),
        (wKnight, bKnight),
    ]

    def __init__(self, color: Color, piece_type: PieceType, DISPLAYSURF, size: int):
        self.position = None
        self.sprite = None
        self.DISPLAYSURF = DISPLAYSURF
        self.size = size

        self.color_is_white = color == Color.WHITE
        self.piece_type = piece_type

        self.setSprite()

    def setPosition(self, position: Position):
        self.position = position

    def setSprite(self):
        self.sprite = self.pieceImages[self.piece_type.value - 1][
            int(not self.color_is_white)
        ]

    def displayPiece(self):
        self.sprite = pygame.transform.scale(self.sprite, (self.size, self.size))
        self.DISPLAYSURF.blit(self.sprite, (self.position.j, self.position.i))
