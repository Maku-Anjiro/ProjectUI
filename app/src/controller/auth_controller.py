from datetime import timedelta

from fastapi import BackgroundTasks, Depends, HTTPException, status
from fastapi import Request
from fastapi.security import OAuth2PasswordRequestForm
from jose import jwt
from starlette.responses import JSONResponse

from app.constants import Constants
from app.src.core.security import AppSecurity
from app.src.database.models import Users
from app.src.database.models.users_model import CreateUser
from app.src.database.repository.user_repositories import UserRepository
from app.src.exceptions.app_exception import (DataBadRequestException, DataBaseDataNotFoundException,
                                              DataForbiddenException, UnAuthorizeAccessException, )

constants = Constants()


class AuthController:
     @classmethod
     async def create_user_account(cls, user_: CreateUser,
                                   background_task: BackgroundTasks,
                                   request: Request):
          """
          To create a user account. Create first, before create its personal information.
          :param request:
          :param user_: that contains the credentials of a user to be insert in database.
          :param background_task: to send email without delaying the process of sending email.
          :return: JSONResponse
          """
          try:
               # retrieved data
               data = await UserRepository.find_user_by_email(user_.email)
               # check if data exists.
               if data:
                    if data.status == 'pending':
                         return DataForbiddenException(message="Please verify your account first.")
                    # return JSONResponse
                    raise DataBadRequestException(message="User is already exists. Please log in instead.")

               # hash password
               new_user = Users(full_name=user_.full_name, phone=user_.phone)
               hashed_password = AppSecurity.hash_password(user_.password.strip())
               new_user.password = hashed_password

               # insert user in database
               await UserRepository.create_user_account(new_user)

               # generate access token
               to_encode = {"user_email": new_user.email, "user_id": new_user.id}

               access_token = AppSecurity.generate_access_token(to_encode)
               # generate activation link

               url_link_for_activation = cls.__generate_activation_link(
                       user_.email, request)

               # send the activation link in email
               # background_task.add_task(EmailServices.send_message_via_clicking, user_.email, url_link_for_activation)
               # return JSONResponse
               return JSONResponse(
                       status_code=status.HTTP_201_CREATED,
                       access_token=access_token,
                       action="signup",
                       message='Successfully created account. Please go to your email to verify it.')
          except Exception as e:
               # if encountered error, then raise it
               raise e

     @staticmethod
     async def authenticate_user(form_data: OAuth2PasswordRequestForm = Depends(), ):
          try:
               headers = {'WWW-Authenticate': "Bearer"}
               # retrieve user data first
               user_ = await  UserRepository.find_user_by_email(form_data.username)
               # check if user exists
               if not user_:
                    raise DataBaseDataNotFoundException(message="User not found.", headers=headers, )

               if user_.status == 'pending':
                    raise DataForbiddenException(message="Account pending approval. Please verify your email.")

               # check if provided password is match to the hash password is db
               if not AppSecurity.verify_hashed_password(form_data.password, user_.password):
                    raise HTTPException(
                            status_code=status.HTTP_400_BAD_REQUEST,
                            detail="Incorrect password.",
                            headers=headers)

               to_encode = {"user_id"   : user_.id,
                            'user_email': user_.email}
               generated_access_token = AppSecurity.generate_access_token(to_encode)

               # return response
               return JSONResponse(
                       status_code=status.HTTP_200_OK,
                       content={"access_token": generated_access_token, "access_type": "Bearer"})

          except Exception as e:
               # if encountered error, then raise it
               raise e

     @staticmethod
     def __generate_activation_link(email: str, request: Request):
          # generate token that will send to param
          generated_token = AppSecurity.generate_access_token({"user_email": email},
                                                              expiration=int(timedelta(days=1).total_seconds()))

          # API Endpoint where to activate the user account
          base_url = str(request.base_url)
          base_url = base_url[:len(base_url) - 1]

          if not base_url.endswith(f':8000'):
               base_url = f"{base_url}:9898"
          url_link_for_activation = f"{base_url}{constants.HTTP_API_PREFIX}/auth/activate/account?token={generated_token}"

          return url_link_for_activation

     @staticmethod
     async def oauth_google_callback(token: str):
          try:
               try:
                    # validate google token
                    user_info = jwt.get_unverified_claims(token)
               except Exception:
                    # raise custom error
                    raise UnAuthorizeAccessException(
                            message="Invalid token, please retry again.",
                            headers={'WWW-Authenticate': "Bearer"})

               # get the email
               user_info = user_info.get("data") if user_info.get("data") else user_info
               print(user_info)
               user_email = str(user_info['email']).strip()
               # get the username or fullname
               full_name = f'{user_info.get('given_name')} {user_info.get('family_name')}'

               # get user via email
               data = await UserRepository.find_user_by_email(user_email)
               # if not existing which is signup
               if not data:
                    # set up the initial data that will set in app later on
                    new_user = Users(full_name=full_name, email=user_email)
                    # generate access token
                    to_encode = {'user_id': new_user.user_id, 'email': new_user.email}
                    # also the refresh token
                    await UserRepository.create_user_account(new_user)
                    generated_access_token = AppSecurity.generate_access_token(to_encode)
                    new_user.status = 'activated'  # because when you login as google it will auto activated
                    # update the previous data to encode

                    # return the data for the meantime, and will insert it in db later in frontend side
                    return JSONResponse(
                            status_code=status.HTTP_201_CREATED,
                            content={"message"     : "Successfully created account.",
                                     "access_token": generated_access_token,
                                     "access_type": "Bearer"})

               # generate access token
               print(data)
               # to_encode = {'user_id': data.user_id, 'email': data.email}
               # also the refresh token

               # generated_access_token = AppSecurity.generate_access_token(to_encode)
               # return response
               return JSONResponse(
                       status_code=status.HTTP_201_CREATED,
                       content={"message"     : "Successfully log in.",
                                "access_token": "generated_access_token",
                                "access_type": "Bearer"})
          except Exception as e:
               raise e
