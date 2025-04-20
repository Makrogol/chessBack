import os
from pathlib import Path
from pydantic import BaseModel
from pydantic_settings import BaseSettings
from dotenv import load_dotenv

BASE_DIR = Path(__file__).parent.parent.parent
load_dotenv(BASE_DIR / ".env")


class EnvVars(BaseModel):
    db_user: str = os.environ.get("DB_USER")
    db_password: str = os.environ.get("DB_PASSWORD")
    db_host: str = os.environ.get("DB_HOST")
    db_port: str = os.environ.get("DB_PORT")
    db_name: str = os.environ.get("DB_NAME")


class DbSettings(BaseModel):
    env_vars: EnvVars = EnvVars()
    url: str = (
        f"postgresql+asyncpg://{env_vars.db_user}:{env_vars.db_password}@{env_vars.db_host}:{env_vars.db_port}/{env_vars.db_name}"
    )
    echo: bool = False
    # echo: bool = True


class AuthJWTSettings(BaseModel):
    private_key_path: Path = BASE_DIR / "certs" / "jwt-private.pem"
    public_key_path: Path = BASE_DIR / "certs" / "jwt-public.pem"
    algorithm: str = "RS256"
    access_token_expire_minutes: int = 60 * 24 * 30  # 30 дней


class Settings(BaseSettings):
    db: DbSettings = DbSettings()
    auth_jwt: AuthJWTSettings = AuthJWTSettings()


settings = Settings()
