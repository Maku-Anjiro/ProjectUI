from typing import Optional

from sqlalchemy import Column, ForeignKey
from sqlmodel import Field, SQLModel


class BaseUserProfileModel(SQLModel):
     # This is generated from cloudinary
     img_url: Optional[str] = Field(nullable=True, default=None)
     public_key: Optional[str] = Field(nullable=True, unique=True, index=True, default=None)


class UserProfile(BaseUserProfileModel, table=True):
     __tablename__ = "users_profile"
     id: int = Field(default=None, primary_key=True, index=True)
     user_id: int = Field(foreign_key="users.user_id",ondelete="CASCADE")


class CreateUserProfile(BaseUserProfileModel):
     pass


class UpdateUserProfile(BaseUserProfileModel):
     pass
