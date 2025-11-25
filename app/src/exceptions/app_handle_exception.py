from fastapi import Request
from starlette.responses import JSONResponse

from app.src.exceptions.app_exception import BaseAppException


def app_exception_handler():
     async def raise_exception_(_: Request, exc: BaseAppException):
          content = {"status_code": exc.status_code,
                     'status'     : exc.message_status,
                     'message'    : exc.message}

          if exc.headers is None:

               return JSONResponse(status_code=exc.status_code,
                                   content=content)

          return JSONResponse(status_code=exc.status_code,
                              content=content,
                              headers=exc.headers)

     return raise_exception_
