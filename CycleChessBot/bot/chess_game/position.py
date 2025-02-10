from enum import Enum

class Position():

    def __init__(self, i: int = -1, j: int = -1) -> None:
        self.i = i
        self.j = j
    
    def __str__(self) -> str:
        return f"{self.i},{self.j}"
