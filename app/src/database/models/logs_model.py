from datetime import datetime
from enum import Enum

from sqlalchemy import Column, ForeignKey, func, String, TIMESTAMP
from sqlmodel import Field, SQLModel,Enum as SQLEnum


class Status(str, Enum):
    Valid = "Valid"
    Invalid = "Invalid"
    Expired = "Expired"
    Duplicate = "Duplicate"

class BaseLogs(SQLModel):
     visitor_id: str = Field(default=None, sa_column=Column(String(length=100),
                                                            ForeignKey("visitors.visitor_id"),
                                                            nullable=False))
     qr_code: str = Field(default=None, sa_column=Column(String(length=100), nullable=False, index=True))
     status: Status = Field(default=None, sa_column=Column(SQLEnum(Status, name="status_enum"), nullable=True))


class Logs(BaseLogs,table=True):
     __tablename__ = "logs"
     log_id : int = Field(default=None, primary_key=True)
     time_stamp : datetime = Field(default=None, sa_column=Column(TIMESTAMP(timezone=True),default=func.current_timestamp(), server_default=func.current_timestamp()))
