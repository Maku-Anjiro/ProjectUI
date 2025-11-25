from datetime import datetime, timedelta, timezone

from jose import jwt
from passlib.context import CryptContext

from app.constants import Constants

constants = Constants()
class AppSecurity:


     __context = CryptContext(schemes=['argon2'], deprecated='auto')

     @classmethod
     def hash_password(cls, plain_password: str):
          """
          To hash password using Argon2.
          :param plain_password: is provided by user.
          :return: hashed password.
          """
          return cls.__context.hash(plain_password)

     @classmethod
     def verify_hashed_password(cls, plain_password: str, hashed_password: str):
          """
          To verify if the inputted password is match to the hashed password.
          :param plain_password: is provided by user.
          :param hashed_password: is a hashed password that will retrieve to db.
          :return: True, if plain and hashed password is matched, otherwise False.
          """
          return cls.__context.verify(plain_password, hashed_password)

     @classmethod
     def generate_access_token(cls, data_: dict,
                               expiration: int = 0):
          to_encode = data_.copy()

          expires = datetime.now(timezone.utc) + timedelta(minutes=1)

          if expiration > 0:
               expires = datetime.now(timezone.utc) + timedelta(expiration)

          # decrypt the data before it encode
          to_encode = {"data"           : to_encode,
                       "exp"            : expires}

          encoded = jwt.encode(to_encode, key=constants.JWT_KEY, algorithm=constants.JWT_ALGORITHM)
          return encoded

