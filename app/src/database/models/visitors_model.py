from datetime import datetime
from enum import Enum

from sqlalchemy import Column, DateTime, func, String, Text, TIMESTAMP, UniqueConstraint
from sqlmodel import Field, SQLModel


class ExpiryStatus(Enum):
    Valid = "Valid"
    Invalid = "Invalid"
    Expired = "Expired"
    Pending = "Pending"

class BaseVisitorModel(SQLModel):
     full_name : str = Field(default=None,sa_column=Column(String(length=100), nullable=False) )
     email : str = Field(default=None,sa_column=Column(String(length=100), nullable=False, index=True))
     phone : str = Field(default=None,sa_column=Column(String(length=20), nullable=True) )
     purpose : str = Field(default=None,sa_column=Column(String(length=100), nullable=True) )
     host : str = Field(default=None,sa_column=Column(String(length=100), nullable=True) )
     notes : str = Field(default=None,sa_column=Column(Text, nullable=True))


class Visitor(BaseVisitorModel, table = True):
     __tablename__ = "visitors"

     qr_code : str = Field(default=None,sa_column=Column(String(length=100), nullable=False, unique=True) )
     visitor_id : int = Field(default=None,primary_key=True, index=True)
     created_at : datetime  = Field(default=None,sa_column=Column(TIMESTAMP(timezone=True), nullable=False, server_default=func.current_timestamp()))
     expiry_at : datetime = Field(default=None,sa_column=Column(DateTime(timezone=True), nullable=False))
     last_status : str = Field(default=ExpiryStatus.Valid.value,sa_column=Column(String(length=20), nullable=True) )
     last_scan : datetime = Field(default=None,sa_column=Column(DateTime(timezone=True), nullable=True, server_default=func.now(), onupdate=func.now()))
     __table_args__ = (
             UniqueConstraint("qr_code", name="idx_visitors_qr"),
     )

class CreateVisitor(BaseVisitorModel):
     pass


