from fastapi.params import Depends
from fastapi.security import OAuth2PasswordBearer
from jose import ExpiredSignatureError

from app.src.core.security import AppSecurity
from app.src.database.repository.user_repositories import UserRepository
from app.src.exceptions.app_exception import DataBaseDataNotFoundException, JWTExpiredException, JWTInvalidException

oauth_token_url = OAuth2PasswordBearer(tokenUrl="/qrgate/login")
class AppDependencies:

     @staticmethod
     async def get_current_user(token : str = Depends(oauth_token_url) ):
          try:
               try:
                    decoded_token = AppSecurity.decode_jwt_token(token)
               except Exception as e:
                    raise e

               # get the user data from decoded token
               payload = decoded_token.get('data')

               # get the user id of user
               user_id = payload.get('user_id')

               current_data = await UserRepository.get_personal_information(user_id)
               if not current_data:
                    raise DataBaseDataNotFoundException

               return current_data[0]

          except ExpiredSignatureError:
               raise JWTExpiredException
          except Exception as e:
               raise JWTExpiredException
