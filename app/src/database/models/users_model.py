from datetime import datetime


from sqlalchemy import Column, DateTime, func, String, Text, TIMESTAMP, UniqueConstraint
from sqlmodel import Field, SQLModel


class BaseUserModel(SQLModel):
     full_name : str = Field(default=None,sa_column=Column(String(length=100), nullable=False) )
     email : str = Field(default=None,sa_column=Column(String(length=100), nullable=False, index=True))
     phone : str = Field(default=None,sa_column=Column(String(length=20), nullable=True) )
     password : str = Field(default=None, sa_column=Column( nullable=True))
     status : str = Field(default="pending")

class Users(BaseUserModel, table = True):
     __tablename__ = "users"
     user_id : int = Field(default=None,primary_key=True, index=True)
     created_at : datetime  = Field(default=None,sa_column=Column(TIMESTAMP(timezone=True), nullable=False, server_default=func.current_timestamp()))


class CreateUser(BaseUserModel):
     pass