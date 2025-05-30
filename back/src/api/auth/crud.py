from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession

from .schemas.users_schemas import (
    UserValidate,
    UserCreate,
    UserElement,
    UserExistValidation,
)
from ...auth import utils
from ...core.models.user import User
from ..game.views import manager


def __convert_users_to_user_elements(users):
    user_elements = []
    for user in users:
        user_elements.append(
            UserElement(
                username=user.username,
                user_available=manager.is_user_available(user.username),
            )
        )
    return user_elements


async def get_users(session: AsyncSession) -> list[UserElement]:
    stmt = select(User).order_by(User.id)
    users = await session.scalars(stmt)
    return __convert_users_to_user_elements(users)


async def delete_user(session: AsyncSession, username: str) -> bool:
    user: User | None = await get_user_by_username(session, username)
    if user is None:
        return False
    stmt = delete(User).where(User.username == username)
    await session.execute(stmt)
    await session.commit()
    return True


async def create_user(session: AsyncSession, user_create: UserCreate) -> User:
    user = User(
        username=user_create.username,
        password=utils.hash_password(user_create.password),
    )
    session.add(user)
    await session.commit()
    return user


async def get_user_by_username(session: AsyncSession, username: str) -> User | None:
    stmt = select(User).where(User.username == username)
    user: User | None = await session.scalar(stmt)
    return user


async def is_user_with_username_exist(
    session: AsyncSession, user: UserExistValidation
) -> bool:
    return await get_user_by_username(session, user.username) is not None


async def validate_user(session: AsyncSession, user_validate: UserValidate) -> bool:
    user = await get_user_by_username(session, user_validate.username)
    if user is None:
        return False

    return utils.validate_password(user_validate.password, user.password)
