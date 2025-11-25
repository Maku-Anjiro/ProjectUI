from fastapi import APIRouter, Body, Depends
from fastapi.params import Query
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.ext.asyncio import AsyncSession

from app.src.controller.auth_controller import AuthController
from app.src.controller.visitors_controller import VisitorsController
from app.src.database.connection import create_session
from app.src.database.models.visitors_model import CreateVisitor

visitor_route = APIRouter(
        prefix='/qrgate',
)


@visitor_route.post("/register")
async def register_visitor(create_visitor: CreateVisitor):
     try:
          return await VisitorsController.register_visitor(create_visitor)
     except Exception as e:
          raise e


@visitor_route.get("/visitors")
async def get_visitors():
     try:
          return await VisitorsController.get_visitors()
     except Exception as e:
          raise e


@visitor_route.get("/paginated/visitors")
async def get_pagination_visitors(skip: int = Query(default=1, ge=1), limit: int = Query(default=10, ge=10),
                                  db: AsyncSession = Depends(create_session)):
     try:
          return await VisitorsController.get_pagination_visitors(skip, limit)
     except Exception as e:
          raise e

@visitor_route.post("/login")
async def visitor_authenticate(form_data : OAuth2PasswordRequestForm = Depends()):
     try:
          return await AuthController.authenticate_user(form_data)
     except Exception as e:
          raise e
@visitor_route.post('/auth/google/callback')
async def oauth_callback(token=Body()):
     """
     A function that retrieve the data after successful linked account in google.
     :param token: a unique token that retrieved from Google after authentication
     :return: JSON Response
     """
     try:
          return await AuthController.oauth_google_callback(token)
     except Exception as e:
          raise e
