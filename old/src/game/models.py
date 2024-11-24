from sqlalchemy import Column, Integer, String

from database import Base

from auth.models import User

class Color(Enum):
   black = "black"
   white = "white"


class Position:
    i = 0
    j = 0

class Turn(Base):
    __tablename__ = "turn"
    
    id = Column(Integer, primary_key=True)
    game_id = Column(Intege, ForeignKey(Game.c.id))
    priviosly_position = Column(Position, nullable=False)
    new_position = Column(Position, nullable=False)
    color_who_done_turn = Column(Color, nullable=False)
    


class Game(Base):
    __tablename__ = "game"

    id = Column(Integer, primary_key=True)
    user_white = Column(Integer, ForeignKey(User.c.id)
    user_black = Column(Integer, ForeignKey(User.c.id)
