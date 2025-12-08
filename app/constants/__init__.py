from pathlib import Path

from dotenv import load_dotenv
from pydantic import EmailStr, SecretStr
from pydantic_settings import BaseSettings
load_dotenv()

class Constants(BaseSettings):
     # JWT
     JWT_KEY: str
     JWT_REFRESH_EXPIRATION: int
     JWT_ALGORITHM: str
     TEMPLATE_PATH: Path = Path(__file__).parent.parent / 'templates' / 'email'


     # Cloudinary Configuration
     C_NAME : str
     C_KEY : str
     C_SECRET : str
     C_SECURE : bool
     SERVER_PORT : int = 8000
     HTTP_API_PREFIX: str = '/qrgate'


     # FILE SIZE LIMIT
     FILE_SIZE_LIMIT: int = 10 * 1024 * 1024

     MAIL_FROM: EmailStr
     MAIL_PASSWORD: SecretStr
     MAIL_PORT: int
     MAIL_STARTTLS: bool
     MAIL_USERNAME: str
     MAIL_SSL_TLS: bool
     MAIL_SERVER: str
     MAIL_USE_CREDENTIALS: bool
     MAIL_VALIDATE_CERTS: bool
