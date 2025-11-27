from sqlmodel import SQLModel

from app.src.database.models.logs_model import Logs
from app.src.database.models.user_profile_photo_model import UserProfile
from app.src.database.models.users_model import Users
from app.src.database.models.visitors_model import Visitor

Base = SQLModel()
__all__ = ['Base',
           "Logs",
           "Visitor",
           'Users',
           "UserProfile"]
