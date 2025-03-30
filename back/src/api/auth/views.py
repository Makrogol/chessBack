from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from .schemas.users_schemas import UserCreate, UserValidate, UserElement, UserExistValidation
from .schemas.responses_schemas import UserCreateResponse, UserValidateResponse, UserDeleteResponse, \
    UserAvailableResponse, TokenValidateResponse
from .schemas.jwt_schemas import JwtPayload
from . import crud
from ...core.models.db_helper import db_helper
from . import jwt_view_utils as jwt_utils
from ..game.views import manager

router = APIRouter(prefix="/auth", tags=["Auth"])


@router.get("/users")
async def get_users(
        session: AsyncSession = Depends(db_helper.scoped_session_dependency),
) -> list[UserElement]:
    return await crud.get_users(session)


@router.get("/available_user")
async def available_user(
        username: str,
) -> UserAvailableResponse:
    return UserAvailableResponse(
        user_available=manager.has_connection(username),
    )


@router.get("/delete_user")
async def delete_user(
        username: str,
        session: AsyncSession = Depends(db_helper.scoped_session_dependency),
) -> UserDeleteResponse:
    return UserDeleteResponse(
        success=await crud.delete_user(session, username),
    )


@router.post("/", response_model=UserCreateResponse)
async def create_user(
        user: UserCreate,
        session: AsyncSession = Depends(db_helper.scoped_session_dependency),
) -> UserCreateResponse:
    response = UserCreateResponse()
    try:
        await crud.create_user(session, user)
        jwt_payload = JwtPayload(
            sub=user.username,
            username=user.username,
        )
        response.token = jwt_utils.create_jwt_token(jwt_payload)
        response.username = user.username
    except Exception as e:
        response.success = False
    return response


@router.get("/validate_user", response_model=UserValidateResponse)
async def validate_user(
        username: str,
        password: str,
        session: AsyncSession = Depends(db_helper.scoped_session_dependency),
) -> UserValidateResponse:
    user_validate = UserValidate(username=username, password=password)
    response = UserValidateResponse()
    response.success = await crud.validate_user(session, user_validate)
    if response.success:
        jwt_payload = JwtPayload(
            sub=user_validate.username,
            username=user_validate.username,
        )
        response.token = jwt_utils.create_jwt_token(jwt_payload)
        response.username = username
    return response


@router.get("/validate_token", response_model=TokenValidateResponse)
async def validate_token(
        user: UserExistValidation | None = Depends(jwt_utils.get_user_from_jwt),
        session: AsyncSession = Depends(db_helper.scoped_session_dependency),
) -> TokenValidateResponse:
    response = TokenValidateResponse()
    if user is None:
        response.success = False
    else:
        response.success = await crud.is_user_with_username_exist(session, user)
        response.username = user.username
    return response
