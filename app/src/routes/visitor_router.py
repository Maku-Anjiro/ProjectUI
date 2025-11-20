from fastapi import APIRouter, Depends

from app.src.controller.register_visitor_controller import RegisterVisitorController
from app.src.database.connection import create_session
from app.src.database.models.visitors_model import CreateVisitor

visitor_route = APIRouter(
        prefix='/qrgate'
)

@visitor_route.post("/register")
async def register_visitor(create_visitor : CreateVisitor, db  = Depends(create_session)):
     try:
          return await RegisterVisitorController.register_visitor(create_visitor,db)
     except Exception as e:
          raise e
