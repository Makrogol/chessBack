from pydantic import BaseModel


class BaseResponse(BaseModel):
    pass

class UserAvailableResponse(BaseResponse):
    user_available: bool
