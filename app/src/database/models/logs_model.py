from datetime import datetime
from enum import Enum

from sqlalchemy import Column, ForeignKey, func, Integer, String, TIMESTAMP
from sqlmodel import Field, SQLModel,Enum as SQLEnum


class LogStatus(str, Enum):
    Valid = "Valid"
    Invalid = "Invalid"
    Expired = "Expired"

class BaseLogs(SQLModel):
     visitors_id: str = Field(default=None, sa_column=Column(Integer,
                                                            ForeignKey("visitors.visitor_id",ondelete="CASCADE"),
                                                            nullable=False))
     qr_code: str = Field(default=None, sa_column=Column(String(length=100), nullable=False, index=True))
     status: LogStatus = Field(default=None, sa_column=Column(SQLEnum(LogStatus, name="status_enum"), nullable=True))


class Logs(BaseLogs,table=True):
     __tablename__ = "logs"
     log_id : int = Field(default=None, primary_key=True)
     time_stamp : datetime = Field(default=None, sa_column=Column(TIMESTAMP(timezone=True),default=func.current_timestamp(), server_default=func.current_timestamp()))
