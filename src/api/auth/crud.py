import asyncio

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from .shemas import UserValidate, UserCreate, UserElement, UserDb
from src.core.models.user import User
from src.auth.utils import hash_password, validate_password


def __convert_users_to_user_elements(users):
    user_elements = []
    for user in users:
        user_elements.append(UserElement(username=user.username))
    return user_elements


async def get_users(session: AsyncSession) -> list[UserElement]:
    stmt = select(UserDb).order_by(User.id)
    users = await session.scalars(stmt)
    return __convert_users_to_user_elements(users)


async def create_user(session: AsyncSession, user_create: UserCreate) -> User:
    user = User(
        username=user_create.username,
        password=hash_password(user_create.password),
    )
    session.add(user)
    await session.commit()
    return user


async def get_user_by_username(session: AsyncSession, username: str) -> User | None:
    stmt = select(User).where(User.username == username)
    user: User | None = await session.scalar(stmt)
    return user


async def is_user_with_username_exist(session: AsyncSession, username: str) -> bool:
    return await get_user_by_username(session, username) is not None


async def validate_user(session: AsyncSession, user_validate: UserValidate) -> bool:
    user = await get_user_by_username(session, user_validate.username)
    if user is None:
        return False

    return validate_password(user_validate.password, user.password)
