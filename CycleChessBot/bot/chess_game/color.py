from enum import Enum

class Color(Enum):
    NO_COLOR = 0
    WHITE = 1
    BLACK = 2


    def __str__(self) -> str:
        return str(self.value)

def get_another_color(color: Color) -> Color:
    return Color.BLACK if color == Color.WHITE else Color.WHITE

def get_all_colors() -> list[Color]:
    return [Color.WHITE, Color.BLACK]
