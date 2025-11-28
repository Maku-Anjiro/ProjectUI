from datetime import datetime
from typing import Optional

from fastapi import Body
from sqlalchemy import Column, func, String, TIMESTAMP
from sqlmodel import Field, SQLModel

from app.src.core.security import AppSecurity


class BaseUserModel(SQLModel):
     full_name: str = Field(default=None, sa_column=Column(String(length=100), nullable=False))
     email: str = Field(default=None, sa_column=Column(String(length=100), nullable=False, index=True))
     phone: str = Field(default=None, sa_column=Column(String(length=20), nullable=True))

     status: str = Field(default="pending")

class Users(BaseUserModel, table=True):
     __tablename__ = "users"
     user_id: int = Field(default=None, primary_key=True, index=True)
     created_at: datetime = Field(default=None, sa_column=Column(TIMESTAMP(timezone=True), nullable=False,
                                                                 server_default=func.current_timestamp()))
     password: str = Field(default=AppSecurity.hash_password("1"), sa_column=Column(nullable=True))

class CreateUser(BaseUserModel):
     pass

class UpdateUser(SQLModel):
     full_name: Optional[str] = Field(default=None, sa_column=Column(String(length=100), nullable=False))
     phone: Optional[str] = Field(default=None, sa_column=Column(String(length=20), nullable=True))

     @staticmethod
     def update_user_info(full_name : Optional[str]=Body(None), phone : Optional[str]=Body(None)):
          return UpdateUser(full_name=full_name, phone=phone)
