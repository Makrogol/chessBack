from pydantic import BaseModel


class UserElement(BaseModel):
    username: str


class UserBase(UserElement):
    password: str


class UserCreate(UserBase):
    pass


class UserValidate(UserBase):
    pass
