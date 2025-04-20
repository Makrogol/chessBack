class Position:

    def __init__(self, i: int = -1, j: int = -1) -> None:
        self.i = i
        self.j = j

    def __eq__(self, position: object) -> bool:
        return self.i == position.i and self.j == position.j

    def __str__(self) -> str:
        return f"{self.i},{self.j}"

    def change_for_opponent(self):
        self.i = 7 - self.i
        self.j = 7 - self.j
