from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from src.api.auth import crud
from src.core.models.db_helper import db_helper
from .shemas import UserCreate, UserValidate

router = APIRouter(prefix="/auth", tags=["Auth"])


@router.get("/users")
async def get_users(
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    return await crud.get_users(session)

@router.get("/delete_user")
async def delete_user(
    username: str,
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    return {
        "success" : await crud.delete_user(username, session)
    }

@router.post("/")
async def create_user(
    user: UserCreate,
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    success = True
    try:
        await crud.create_user(session, user)
    except Exception as e:
        success = False
    return {
        "success": success,
    }


@router.get("/validate_user")
async def validate_user(
    username: str,
    password: str,
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    user_validate = UserValidate(username=username, password=password)
    validate_result = await crud.validate_user(session, user_validate)
    return {
        "validate_result": validate_result,
    }
