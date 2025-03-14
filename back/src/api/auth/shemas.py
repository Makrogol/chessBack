from pydantic import BaseModel

# Users

class UserElement(BaseModel):
    username: str

class UserExistValidation(UserElement):
    pass

class UserBase(UserElement):
    password: str


class UserCreate(UserBase):
    pass


class UserValidate(UserBase):
    pass


# View responses

class BaseResponse(BaseModel):
    success: bool = True

class BaseTokenResponse(BaseModel):
    token: str | None = None
    token_type: str = "Bearer"

class UserCreateResponse(BaseTokenResponse, BaseResponse):
    pass

class UserValidateResponse(BaseTokenResponse, BaseResponse):
    pass

class TokenValidateResponse(BaseResponse):
    pass

class UserDeleteResponse(BaseResponse):
    pass


# Jwt

class JwtPayload(BaseModel):
    sub: str
    username: str
