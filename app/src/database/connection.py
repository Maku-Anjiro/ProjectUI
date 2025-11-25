from contextlib import asynccontextmanager
from typing import Any, AsyncGenerator

from sqlalchemy.ext.asyncio import async_sessionmaker, AsyncSession, create_async_engine

DB_URL = "mysql+aiomysql://root:1084@localhost/qrgate_db"

engine = create_async_engine(DB_URL)

LocalSession = async_sessionmaker(
        bind=engine,
        autoflush=False,
        expire_on_commit=False)

@asynccontextmanager
async def create_session() -> AsyncGenerator[AsyncSession, Any]:
     async with LocalSession() as db:
          try:
               yield db
               await db.commit()
          except Exception as e:
               await db.rollback()
               raise e


