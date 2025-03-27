from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jwt import InvalidTokenError

from .schemas.jwt_schemas import JwtPayload
from .schemas.users_schemas import UserExistValidation
from ...auth import utils

http_bearer = HTTPBearer(auto_error=False)

def create_jwt_token(pyload: JwtPayload) ->str:
    return utils.encode_jwt(payload=pyload)

def decode_jwt_token(token: str) -> dict:
    return utils.decode_jwt(token)

def get_token_payload(
        credentials: HTTPAuthorizationCredentials | None = Depends(http_bearer),
) -> dict | None:
    if credentials is None:
        return None
    token = credentials.credentials
    try:
        payload = decode_jwt_token(
            token=token,
        )
        return payload
    except InvalidTokenError as e:
        return None

def get_user_from_jwt(
        payload: dict | None = Depends(get_token_payload),
) -> UserExistValidation | None:
    if payload is None:
        return None
    username: str | None = payload.get("sub")
    if username is not None:
        return UserExistValidation(username=username)
    return None