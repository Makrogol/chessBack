from datetime import datetime
from enum import Enum
from typing import List, Optional, Union

from fastapi_cache import FastAPICache
from fastapi_cache.backends.redis import RedisBackend

from fastapi_users import fastapi_users
from pydantic import Field

from fastapi import FastAPI, Depends

from auth.manager import get_user_manager
from auth.schemas import UserRead, UserCreate
from auth.models import User

from auth.base_config import auth_backend, fastapi_users, current_user

app = FastAPI(
    title="Chess backend"
)

app.include_router(
    fastapi_users.get_auth_router(auth_backend),
    prefix="/auth/jwt",
    tags=["auth"],
)

app.include_router(
    fastapi_users.get_register_router(UserRead, UserCreate),
    prefix="/auth",
    tags=["auth"],
)
# TODO check another routers https://fastapi-users.github.io/fastapi-users/10.1/configuration/full-example/

@app.get("/protected-route")
def protected_route(user: User = Depends(current_user)):
    return f"Hello, {user.username}"


@app.get("/unprotected-route")
def unprotected_route():
    return f"Hello, anonym"


# TODO подумать над кешированием истории (или списка пользователей)
@app.get("/users")
# @cache(expire=30) #30 sec будут храниться данные в редисе в кеше
def get_all_users():
    return fastapi_users


@app.on_event("startup")
async def startup_event():
    redis = aioredis.from_url("redis://localhost", encoding="utf8", decode_responses=True)
    FastAPICache.init(RedisBackend(redis), prefix="fastapi-cache")