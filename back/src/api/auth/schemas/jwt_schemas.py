from pydantic import BaseModel


class JwtPayload(BaseModel):
    sub: str
    username: str
