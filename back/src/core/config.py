from pathlib import Path
from pydantic import BaseModel
from pydantic_settings import BaseSettings


class DbSettings(BaseModel):
    url: str = "postgresql+asyncpg://makrogol:1234@db:5432/chess_db"
    echo: bool = False
    # echo: bool = True

class Settings(BaseSettings):
    # api_v1_prefix: str = "/api/v1"

    db: DbSettings = DbSettings()


settings = Settings()
