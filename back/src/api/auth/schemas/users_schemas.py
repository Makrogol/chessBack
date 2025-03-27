from pydantic import BaseModel


class UserWithUsername(BaseModel):
    username: str


class UserElement(UserWithUsername):
    user_available: bool


class UserExistValidation(UserWithUsername):
    pass


class UserBase(UserWithUsername):
    password: str


class UserCreate(UserBase):
    pass


class UserValidate(UserBase):
    pass
