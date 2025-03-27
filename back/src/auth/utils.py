import hashlib
from datetime import datetime, timedelta
import jwt

from ..api.auth.schemas.jwt_schemas import JwtPayload
from ..core.config import settings


def hash_password(password: str) -> bytes:
    return hashlib.md5(password.encode("utf-8")).digest()


def validate_password(password: str, hashed_password: bytes) -> bool:
    return hash_password(password) == hashed_password


def encode_jwt(
        payload: JwtPayload,
        private_key: str = settings.auth_jwt.private_key_path.read_text(),
        algorithm: str = settings.auth_jwt.algorithm,
        expire_minutes: int = settings.auth_jwt.access_token_expire_minutes,
        expire_timedelta: timedelta | None = None,
):
    to_encode = vars(payload)
    now = datetime.utcnow()
    if expire_timedelta:
        expire = now + expire_timedelta
    else:
        expire = now + timedelta(minutes=expire_minutes)
    to_encode.update(
        exp=expire,
        iat=now,
    )

    encoded = jwt.encode(
        to_encode,
        private_key,
        algorithm=algorithm,
    )
    return encoded


def decode_jwt(
        token: str | bytes,
        public_key: str = settings.auth_jwt.public_key_path.read_text(),
        algorithm: str = settings.auth_jwt.algorithm,
) -> dict:
    decoded = jwt.decode(
        token,
        public_key,
        algorithms=[algorithm],

    )
    return decoded
