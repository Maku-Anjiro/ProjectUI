from sqlalchemy import select
from sqlmodel import desc

from app.src.database.connection import create_session
from app.src.database.models import Logs, Visitor
from app.src.database.models.users_model import Users


class UserRepository:

     @staticmethod
     async def find_user_by_email(email):
          async with create_session() as db:
               try:
                    stmt = select(Users).where(Users.email == email)
                    result = await db.execute(stmt)
                    data = result.scalar()
                    return data
               except Exception as e:
                    raise e


     @staticmethod
     async def get_visitors():
          async with create_session() as db:
               try:
                    query = select(
                            Visitor.visitor_id,
                            Visitor.full_name,
                            Visitor.email,
                            Visitor.purpose,
                            Visitor.host,
                            Visitor.qr_code,
                            Visitor.expiry_at,
                            Visitor.last_status,
                            Visitor.last_scan,
                            Visitor.created_at,
                            Visitor.phone,
                            Visitor.notes,
                    ).order_by(desc(column=Visitor.visitor_id))
                    result = await db.execute(query)
                    visitors = result.mappings().all()
                    return visitors
               except Exception as e:
                    raise e

     @staticmethod
     async def paginated_visitors(limit, skip):
          async with create_session() as db:
               try:
                    offset = (skip - 1) * limit
                    query = select(Visitor.visitor_id,
                                   Visitor.full_name,
                                   Visitor.email,
                                   Visitor.purpose,
                                   Visitor.host,
                                   Visitor.qr_code,
                                   Visitor.expiry_at,
                                   Visitor.last_status,
                                   Visitor.last_scan,
                                   Visitor.created_at,
                                   Visitor.phone,
                                   Visitor.notes,
                                   ).limit(limit=limit).offset(offset=offset).order_by(desc(column=Visitor.visitor_id))
                    result = await db.execute(query)
                    visitors = result.mappings().all()
                    return visitors
               except Exception as e:
                    raise e

     @staticmethod
     async def find_qr_code(qr_code):
          async with create_session() as db:
               try:
                    query = select(Visitor).where(Visitor.qr_code == qr_code)
                    result = await db.execute(query)
                    visitor = result.first()
                    return visitor
               except Exception as e:
                    raise e

     @staticmethod
     async def insert_visitor_log(logs : Logs):
          async  with create_session() as db:
               try:
                    db.add(logs)
                    await db.refresh(logs)
               except Exception as e:
                    raise e

     @staticmethod
     async def create_visitor(visitor: Visitor):
          async  with create_session() as db:
               try:
                    db.add(visitor)
               except Exception as e:
                    raise e

     @staticmethod
     async def create_user_account(user: Users):
          async  with create_session() as db:
               try:
                    db.add(user)
               except Exception as e:
                    raise e