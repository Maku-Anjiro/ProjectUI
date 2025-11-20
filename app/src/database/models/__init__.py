from sqlmodel import SQLModel

from app.src.database.models.logs_model import Logs
from app.src.database.models.visitors_model import Visitor
Base = SQLModel()
__all__ = ['Base',
           "Logs",
           "Visitor"]
