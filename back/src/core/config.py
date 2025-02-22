from pathlib import Path
from pydantic import BaseModel
from pydantic_settings import BaseSettings

BASE_DIR = Path(__file__).parent.parent

DB_PATH = BASE_DIR / "users.db"


class DbSettings(BaseModel):
    # url: str = f"sqlite+aiosqlite:///{DB_PATH}"
    url: str = "postgresql+asyncio://makrogol:1234@127.0.0.1:5432/chess_db"
    echo: bool = False
    # echo: bool = True


class Settings(BaseSettings):
    # api_v1_prefix: str = "/api/v1"

    db: DbSettings = DbSettings()


settings = Settings()
