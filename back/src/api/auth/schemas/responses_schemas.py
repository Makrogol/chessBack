from pydantic import BaseModel


class BaseResponse(BaseModel):
    pass


class UsernameResponse(BaseResponse):
    username: str = ""


class SuccessResponse(BaseResponse):
    success: bool = True


class BaseTokenResponse(BaseResponse):
    token: str | None = None
    token_type: str = "Bearer"


class UserCreateResponse(BaseTokenResponse, SuccessResponse, UsernameResponse):
    pass


class UserValidateResponse(BaseTokenResponse, SuccessResponse, UsernameResponse):
    pass


class TokenValidateResponse(SuccessResponse, UsernameResponse):
    pass


class UserDeleteResponse(SuccessResponse):
    pass


class UserAvailableResponse(BaseResponse):
    user_available: bool = False
