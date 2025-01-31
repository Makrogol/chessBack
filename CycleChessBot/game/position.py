from enum import Enum

class Position():

    def __init__(self, i: int = 0, j: int = 0) -> None:
        self.i = 0
        self.j = 0
    
    def __str__(self) -> str:
        return f"{self.i},{self.j}"