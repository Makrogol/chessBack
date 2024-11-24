from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select, insert
from sqlalchemy.ext.asyncio import AsyncSession

from database import get_async_session
from game_history.models import game_history
from game_history.schemas import GameHistoryCreate

router = APIRouter(
    prefix="/game_history",
    tags=["Game history"]
)

# TODO обернуть все ручки в try-except
# TODO сделать ограничение по получаемым записям
@router.get("/")
async def get_game_history_for_user_id(user_id: int, session: AsyncSession = Depends(get_async_session)):
    try:
        query = select(game_history).where(game_history.c.user_white == user_id or game_history.c.user_black == user_id)
        result = await session.execute(query)
        return 
            "status": "success",
            "data": result.all(),
            "details": None,
        }
    except:
        raise HTTPException(status_code=500, detail={
            "status": "error",
            "data": None,
            "details": None,
        })


@router.post("/")
async def add_specific_operations(new_game: GameHistoryCreate, session: AsyncSession = Depends(get_async_session)):
    stmt = insert(game_history).values(**new_game.dict())
    await session.execute(stmt)
    await session.commit()
    return {"status": 200}