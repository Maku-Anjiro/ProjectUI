import uvicorn
from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware

from app.src.routes.visitor_router import visitor_route

app = FastAPI()

app.add_middleware(CORSMiddleware,
                   allow_origins=['*'],
                   allow_methods=['*'],
                   allow_headers=['*'],
                   allow_credentials=True)

app.include_router(visitor_route)





if __name__ == '__main__':
    uvicorn.run("app.main:app", reload=True, host="0.0.0.0",port=8000)