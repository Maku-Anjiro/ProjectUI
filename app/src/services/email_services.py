import time
from threading import Lock

from fastapi_mail import ConnectionConfig, FastMail, MessageSchema, MessageType

from app.constants import Constants

constants = Constants()


class EmailServices:
     __instance = {}
     __lock = Lock()

     def __new__(cls, *args, **kwargs):
          # we lock the thread and check if there is an instance of object
          with cls.__lock:
               if cls not in cls.__instance:
                    instance = super().__new__(*args, **kwargs)
                    time.sleep(1)
                    cls.__instance[cls] = instance
          return cls.__instance[cls]

     @classmethod
     def get_fastapi_mail(cls):
          configuration = ConnectionConfig(
                  MAIL_USERNAME=constants.MAIL_USERNAME,
                  MAIL_PASSWORD=constants.MAIL_PASSWORD,
                  MAIL_FROM=constants.MAIL_FROM,
                  MAIL_PORT=constants.MAIL_PORT,
                  MAIL_SERVER=constants.MAIL_SERVER,
                  MAIL_STARTTLS=constants.MAIL_STARTTLS,
                  MAIL_SSL_TLS=constants.MAIL_SSL_TLS,
                  USE_CREDENTIALS=constants.MAIL_USE_CREDENTIALS,
                  VALIDATE_CERTS=constants.MAIL_VALIDATE_CERTS,
                  TEMPLATE_FOLDER=constants.TEMPLATE_PATH,
          )
          fm = FastMail(configuration)
          return fm

     @classmethod
     async def send_message_via_clicking(cls, recipient: str, activation_link: str, subject="Account Verification",email_template = "email-verification.html" ):
          fm = cls.get_fastapi_mail()
          message = MessageSchema(
                  subject=subject,
                  recipients=[recipient],
                  template_body={"activation_api_link": activation_link,"email":recipient},
                  subtype=MessageType.html,
          )
          await fm.send_message(message, template_name=email_template)

     @classmethod
     async def send_message_via_code(cls, recipient: str, verification_code: int, subject="Account Verification", ):
          fm = cls.get_fastapi_mail()
          message = MessageSchema(
                  subject=subject,
                  recipients=[recipient],
                  template_body={"activation_code": verification_code},
                  subtype=MessageType.html,
          )
          await fm.send_message(message, template_name="email-verification.html")
