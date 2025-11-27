from dotenv import load_dotenv
from pydantic_settings import BaseSettings
load_dotenv()

class Constants(BaseSettings):
     # JWT
     JWT_KEY: str
     JWT_REFRESH_EXPIRATION: int
     JWT_ALGORITHM: str

     # Cloudinary Configuration
     C_NAME : str
     C_KEY : str
     C_SECRET : str
     C_SECURE : bool


     # FILE SIZE LIMIT
     FILE_SIZE_LIMIT: int = 10 * 1024 * 1024