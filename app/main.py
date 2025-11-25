import uvicorn
from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware

from app.src.exceptions.app_exception import BaseAppException
from app.src.exceptions.app_handle_exception import app_exception_handler
from app.src.routes.visitor_router import visitor_route

app = FastAPI()

app.add_middleware(CORSMiddleware,
                   allow_origins=['*'],
                   allow_methods=['*'],
                   allow_headers=['*'],
                   allow_credentials=True)

app.include_router(visitor_route)
app.add_exception_handler(exc_class_or_status_code=BaseAppException, handler=app_exception_handler())






if __name__ == '__main__':
    uvicorn.run("app.main:app", reload=True, host="0.0.0.0",port=8000)