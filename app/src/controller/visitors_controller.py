import secrets
from datetime import date, datetime, timedelta, timezone
from typing import Optional
from zoneinfo import ZoneInfo

from fastapi import BackgroundTasks, HTTPException, Request, status, UploadFile
from fastapi.encoders import jsonable_encoder
from fastapi.params import File
from jose import ExpiredSignatureError
from starlette.responses import JSONResponse
from starlette.templating import Jinja2Templates

from app.constants import Constants
from app.src.core.security import AppSecurity
from app.src.database.models import Logs, Users, Visitor
from app.src.database.models.logs_model import LogStatus
from app.src.database.models.users_model import UpdateUser
from app.src.database.models.visitors_model import CreateVisitor, ExpiryStatus
from app.src.database.repository.user_repositories import UserRepository
from app.src.exceptions.app_exception import DataBadRequestException, JWTInvalidException
from app.src.services.cloudinary_services import CloudinaryServices
from app.src.services.email_services import EmailServices
from app.src.utils import GlobalUtils
from app.src.validation.validate_fields import *

constants = Constants()

templates = Jinja2Templates(directory=constants.TEMPLATE_PATH)


class VisitorsController:

     @staticmethod
     async def register_visitor(register_visitor: CreateVisitor):
          try:
               # Extract fields
               full_name = register_visitor.full_name
               email = register_visitor.email
               phone = register_visitor.phone

               # VALIDATE
               name_check = validate_full_name(full_name)
               if not name_check["valid"]:
                    raise HTTPException(status_code=400, detail=name_check["msg"])

               email_check = validate_email(email)
               if not email_check["valid"]:
                    raise HTTPException(status_code=400, detail=email_check["msg"])

               phone_check = validate_ph_phone(phone)
               if not phone_check["valid"]:
                    raise HTTPException(status_code=400, detail=phone_check["msg"])

               # SANITIZE
               full_name = sanitize_name(full_name)
               email = sanitize_email(email)
               phone = sanitize_phone(phone)

               # Generate QR code (16 char hex)
               qr_code = secrets.token_hex(8)

               # Expiry: 1 day from now
               expiry_at = datetime.now(timezone.utc) + timedelta(days=1)

               # Insert into DB
               visitor = Visitor(
                       full_name=full_name,
                       email=email,
                       phone=phone,
                       purpose=register_visitor.purpose,
                       host=register_visitor.host,
                       notes=register_visitor.notes,
                       qr_code=qr_code,
                       expiry_at=expiry_at)

               await UserRepository.create_visitor(visitor)

               return {
                       "ok"          : True,
                       "email"       : email,
                       "phone"       : phone,
                       "purpose"     : register_visitor.purpose,
                       "qr_code"     : qr_code,
                       "host"        : register_visitor.host,
                       "notes"       : register_visitor.notes,
                       "expiry_at"   : expiry_at,
                       "visitor_id"  : visitor.visitor_id,
                       "visitor_name": full_name,
                       "current_time": datetime.now(timezone.utc)
               }

          except Exception as e:
               raise HTTPException(status_code=500, detail=str(e))

     @staticmethod
     async def log_exit(
             qr_code: str, ):

          if not qr_code:
               raise HTTPException(status_code=400, detail="QR code is required")

          ph_tz = ZoneInfo("Asia/Manila")
          current_time_dt = datetime.now(ph_tz)
          current_time = current_time_dt.strftime("%Y-%m-%d %H:%M:%S")
          visitor = await UserRepository.find_qr_code(qr_code)

          if not visitor:
               raise HTTPException(
                       status_code=404,
                       detail="QR code not found. Please try again.",
               )

          if visitor.exit_time:
               formatted_exit = visitor.exit_time.strftime("%b %d, %Y %I:%M %p")

               raise HTTPException(
                       status_code=400,
                       detail=f"This visitor already logged an exit at {formatted_exit}",
               )

          expiry_dt = visitor.expiry_at.replace(tzinfo=ph_tz)
          now_dt = datetime.now(ph_tz)

          if expiry_dt < now_dt:
               raise HTTPException(
                       status_code=400,
                       detail="QR code has expired. Cannot log exit.",
               )

          visitor.exit_time = current_time_dt
          visitor.last_status = "Exited"
          visitor.last_scan = current_time_dt
          log = Logs(
                  visitor_id=visitor.visitor_id,
                  qr_code=visitor.qr_code,
                  status=LogStatus.Valid,
                  timestamp=current_time_dt,
          )
          await UserRepository.insert_visitor_log(log)

          return {
                  "ok"          : True,
                  "msg"         : "Exit logged successfully! Thank you for visiting.",
                  "visitor_id"  : visitor.visitor_id,
                  "visitor_name": visitor.full_name,
                  "email"       : visitor.email,
                  "phone"       : visitor.phone,
                  "purpose"     : visitor.purpose,
                  "host"        : visitor.host,
                  "entry_time"  : GlobalUtils.format_time(
                          visitor.created_at.strftime("%Y-%m-%d %H:%M:%S")),
                  "exit_time"   : GlobalUtils.format_time(current_time),
                  "duration"    : GlobalUtils.calculate_duration(
                          visitor.created_at.strftime("%Y-%m-%d %H:%M:%S"),
                          current_time,
                  )
          }

     @staticmethod
     async def get_visitors():
          try:
               visitors = await UserRepository.get_visitors()
               total_visitors = len(visitors)
               valid_qr_code = len(list(filter(lambda x: x.last_status == ExpiryStatus.Valid.value, visitors)))
               expired_qr_code = len(list(filter(lambda x: x.last_status == ExpiryStatus.Expired.value, visitors)))
               pending_qr_code = len(list(filter(lambda x: x.last_status == ExpiryStatus.Pending.value, visitors)))

               return {
                       "ok"             : True,
                       "total_visitors" : total_visitors,
                       "valid_qr_code"  : valid_qr_code,
                       "expired_qr_code": expired_qr_code,
                       "pending_qr_code": pending_qr_code,
                       "data"           : jsonable_encoder(visitors)
               }

          except Exception as e:
               raise HTTPException(status_code=500, detail=str(e))

     @staticmethod
     async def get_pagination_visitors(skip, limit):
          try:
               visitors = await UserRepository.paginated_visitors(limit, skip)
               return {
                       "ok"  : True,
                       "data": jsonable_encoder(visitors)
               }

          except Exception as e:
               raise HTTPException(status_code=500, detail=str(e))

     @staticmethod
     async def update_email(token: str, request: Request):
          contex = {'email_address': "",
                    'date_today'   : "",
                    'verification' : "pending"}
          try:
               try:
                    payload = AppSecurity.decode_jwt_token(token)
               except Exception as e:
                    raise JWTInvalidException

               current_email = payload.get('data').get("user_email")
               data = await UserRepository.find_user_by_email(current_email)

               if not data:
                    raise DataBadRequestException("Email is not found.")

               # get the old and new email

               contex['old_email_address'] = current_email

               new_email = payload.get('data').get('new_email')

               contex['new_email_address'] = new_email

               contex['date_today'] = GlobalUtils.format_created_at(date.today())
               # check both email are the same, it means it is already updated email, then return

               if current_email == new_email:
                    return templates.TemplateResponse(request=request, context=contex,
                                                      name="email-already-updated.html")
               # update the user email
               await UserRepository.update_user_email(current_email, new_email)
               return templates.TemplateResponse(request=request, context=contex, name="updated-email.html")

          except ExpiredSignatureError as e:
               error_response_expired = {
                       "title"  : "Verification Link Has Expired",
                       "message": "The link you clicked is no longer valid. For security reasons, verification links automatically expire after <strong>24 hours</strong>.",
                       "reasons": [
                               "More than 24 hours have passed since the email was sent",
                               "The link was already used once already",
                               "The link was manually invalidated"
                       ]
               }
               contex.update({"error_response": error_response_expired})
               return templates.TemplateResponse(request=request, context=contex, name="failed-email-verification.html")
          except DataBadRequestException:
               error_response_not_found = {
                       "title"  : "Email Address Not Found",
                       "message": "We couldn’t find an account associated with this email address. Please check that you entered it correctly or sign up for a new account.",
                       "reasons": [
                               "The email is not registered in our system",
                               "You may have typed the email incorrectly",
                               "The account was previously deleted"
                       ]
               }
               contex.update({"error_response": error_response_not_found})
               return templates.TemplateResponse(request=request, context=contex, name="failed-email-verification.html")

          except Exception as e:
               error_response_unexpected = {
                       "title"  : "Unexpected Error Occurred",
                       "message": "We’re experiencing technical difficulties at the moment. Our team has been notified and is working to fix it. Please try again in a few minutes.",
                       "reasons": [
                               "A temporary system issue occurred",
                               "High load on the server",
                               "An unexpected bug was triggered"
                       ]
               }
               contex.update({"error_response": error_response_unexpected})
               return templates.TemplateResponse(request=request, context=contex, name="failed-email-verification.html")


     @classmethod
     async def update_full_user_information(cls,
                                            current_user,
                                            users_info: UpdateUser,
                                            email,
                                            request: Request,
                                            background_task: BackgroundTasks,
                                            img_file: Optional[UploadFile] = File(None),
                                            ):
          try:

               user_obj: Users = current_user.get("Users")
               current_user_id = user_obj.user_id
               is_email_change = False

               # map the fields into orig data
               updated_data = user_obj.model_copy(
                       update=users_info.model_dump(exclude_unset=True, exclude_none=True))

               # check if
               current_email = user_obj.email
               new_email = email

               if current_email != new_email:
                    is_email_change = True
                    # generate a url to update the email address of user
                    url_link = cls.__generate_update_email_link(current_email, new_email, request)
                    background_task.add_task(EmailServices.send_message_via_clicking,
                                             new_email, url_link, "Request email update", )
               # then send
               # update user information
               await UserRepository.update_personal_information(current_user_id, updated_data.model_dump())

               # update user profile
               profile_image = current_user.get("UserProfile")
               await cls.__update_user_profile_image(current_user_id,
                                                     profile_image, img_file)
               message = (
                    'Successfully updated information, please go to your email to verify your new email.' if is_email_change
                    else "Successfully updated information .")

               return JSONResponse(status_code=status.HTTP_200_OK,
                                   content={"message": message})
          except Exception as e:
               raise e

     @staticmethod
     async def __update_user_profile_image(user_id, profile_image, image_file: UploadFile = File(None)):
          try:
               current_data = await UserRepository.get_personal_information(user_id)

               if not current_data:
                    raise DataBadRequestException

               profile_old_profile_picture = profile_image.public_key if \
                    profile_image else None
               if GlobalUtils.is_file_uploaded(img_file=image_file):
                    filename = image_file.filename
                    if image_file and image_file.size > constants.FILE_SIZE_LIMIT:
                         raise DataBadRequestException(status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
                                                       message_status="error",
                                                       message="File too large. Maximum size is 10MB.")

                    profile_image = await CloudinaryServices.update_image_file(user_id,
                                                                               filename,
                                                                               profile_old_profile_picture,
                                                                               image_file.file.read())

                    await UserRepository.update_profile_image(user_id, profile_image)
          except Exception as e:
               raise e


     @staticmethod
     def __generate_update_email_link(current_email: str, new_email: str, request: Request):
          # generate token that will send to param
          generated_token = AppSecurity.generate_access_token({"user_email"        : current_email
                                                                      , "new_email": new_email},
                                                              expiration=1)

          # API Endpoint where to activate the user account
          base_url = str(request.base_url)
          base_url = base_url[:len(base_url) - 1]

          if not base_url.endswith(f':{constants.SERVER_PORT}'):
               base_url = f"{base_url}:{constants.SERVER_PORT}"
          url_link_for_activation = f"{base_url}{constants.HTTP_API_PREFIX}/email?token={generated_token}"

          return url_link_for_activation
