from fastapi import Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jwt import InvalidTokenError

from .shemas import JwtPayload, UserExistValidation

from ...auth import utils

http_bearer = HTTPBearer()

def create_jwt_token(pyload: JwtPayload) ->str:
    return utils.encode_jwt(payload=pyload)

def decode_jwt_token(token: str) -> dict:
    return utils.decode_jwt(token)

def get_token_payload(
        credentials: HTTPAuthorizationCredentials = Depends(http_bearer),
) -> dict | None:
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