from pydantic import BaseModel


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

class BaseTokenResponse(BaseModel):
    token: str | None = None
    token_type: str = "Bearer"

class UserCreateResponse(BaseTokenResponse):
    success: bool = True

class UserValidateResponse(BaseTokenResponse):
    success: bool = True

class TokenValidateResponse(BaseModel):
    success: bool = True
