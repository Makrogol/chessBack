from pydantic import BaseModel


class BaseResponse(BaseModel):
    pass


class SuccessResponse(BaseResponse):
    success: bool = True


class BaseTokenResponse(BaseResponse):
    token: str | None = None
    token_type: str = "Bearer"
    username: str = ""


class UserCreateResponse(BaseTokenResponse, SuccessResponse):
    pass


class UserValidateResponse(BaseTokenResponse, SuccessResponse):
    pass


class TokenValidateResponse(SuccessResponse):
    pass


class UserDeleteResponse(SuccessResponse):
    pass


class UserAvailableResponse(BaseResponse):
    user_available: bool
