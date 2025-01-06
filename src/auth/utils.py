import hashlib


def hash_password(password: str) -> bytes:
    return hashlib.md5(password.encode("utf-8")).digest()


def validate_password(password: str, hashed_password: bytes) -> bool:
    return hash_password(password) == hashed_password
