from fastapi import APIRouter, Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jwt import InvalidTokenError
from sqlalchemy.ext.asyncio import AsyncSession

from ...api.auth import crud
from ...core.models.db_helper import db_helper
from .shemas import UserCreate, UserValidate, UserCreateResponse, UserValidateResponse, TokenValidateResponse, \
    UserExistValidation

router = APIRouter(prefix="/auth", tags=["Auth"])

http_bearer = HTTPBearer()

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
        "success" : await crud.delete_user(session, username)
    }

@router.post("/", response_model=UserCreateResponse)
async def create_user(
    user: UserCreate,
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    response = UserCreateResponse()
    try:
        await crud.create_user(session, user)
        jwt_payload = {
            # subject
            "sub": user.username,
            "username": user.username,
        }
        response.token = crud.create_jwt_token(jwt_payload)
    except Exception as e:
        response.success = False
    return response

@router.get("/validate_user", response_model=UserValidateResponse)
async def validate_user(
    username: str,
    password: str,
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    user_validate = UserValidate(username=username, password=password)
    response = UserValidateResponse()
    response.success = await crud.validate_user(session, user_validate)
    if response.success:
        jwt_payload = {
            # subject
            "sub": user_validate.username,
            "username": user_validate.username,
        }
        response.token = crud.create_jwt_token(jwt_payload)
    return response


def get_current_token_payload(
        credentials: HTTPAuthorizationCredentials = Depends(http_bearer),
) -> dict | None:
    print(credentials)
    token = credentials.credentials
    try:
        payload = crud.decode_jwt_token(
            token=token,
        )
        print(payload)
        return payload
    except InvalidTokenError as e:
        return None

def get_current_auth_user(
        payload: dict | None = Depends(get_current_token_payload),
) -> UserExistValidation | None:
    if payload is None:
        return None
    username: str | None = payload.get("sub")
    if username is not None:
        return UserExistValidation(username=username)
    return None

@router.get("/validate_token", response_model=TokenValidateResponse)
async def validate_token(
    user: UserExistValidation | None = Depends(get_current_auth_user),
    session: AsyncSession = Depends(db_helper.scoped_session_dependency),
):
    response = TokenValidateResponse()
    if user is None:
        response.success = False
    else:
        response.success = await crud.is_user_with_username_exist(session, user)
    return response
