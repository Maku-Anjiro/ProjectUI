from dotenv import load_dotenv
from pydantic import EmailStr, SecretStr
from pydantic_settings import BaseSettings
load_dotenv()

class Constants(BaseSettings):
     # JWT
     JWT_KEY: str
     JWT_REFRESH_EXPIRATION: int
     JWT_ALGORITHM: str
