from fastapi import APIRouter, Depends
from fastapi.params import Query
from sqlalchemy.ext.asyncio import AsyncSession

from app.src.controller.visitors_controller import VisitorsController
from app.src.database.connection import create_session
from app.src.database.models.visitors_model import CreateVisitor

visitor_route = APIRouter(
        prefix='/qrgate',
)


@visitor_route.post("/register")
async def register_visitor(create_visitor: CreateVisitor, db=Depends(create_session)):
     try:
          return await VisitorsController.register_visitor(create_visitor, db)
     except Exception as e:
          raise e


@visitor_route.get("/visitors")
async def get_visitors(db: AsyncSession = Depends(create_session)):
     try:
          return await VisitorsController.get_visitors(db)
     except Exception as e:
          raise e


@visitor_route.get("/paginated/visitors")
async def get_pagination_visitors(skip: int = Query(default=1, ge=1), limit: int = Query(default=10, ge=10),
                                  db: AsyncSession = Depends(create_session)):
     try:
          return await VisitorsController.get_pagination_visitors(skip, limit, db)
     except Exception as e:
          raise e
