from datetime import datetime
from zoneinfo import ZoneInfo

from app.src.exceptions.app_exception import DataBadRequestException


class GlobalUtils:
     @staticmethod
     def make_first_letter_to_capital(field: str):
          full_name = field.strip().lower().split(" ")
          formatted_full_name = ""
          for name in full_name:
               formatted_full_name += name[0].upper() + "" + name[1:] + " "

          return formatted_full_name

     @staticmethod
     def format_time(dt_str: str):
          dt = datetime.strptime(dt_str, "%Y-%m-%d %H:%M:%S")
          return dt.strftime("%b %d, %Y %I:%M %p")

     @staticmethod
     def calculate_duration(start: str, end: str):
          ph_tz = ZoneInfo("Asia/Manila")

          start_dt = datetime.strptime(start, "%Y-%m-%d %H:%M:%S").replace(tzinfo=ph_tz)
          end_dt = datetime.strptime(end, "%Y-%m-%d %H:%M:%S").replace(tzinfo=ph_tz)

          diff = end_dt - start_dt

          hours = diff.seconds // 3600
          minutes = (diff.seconds % 3600) // 60

          if hours > 0:
               return f"{hours} hour{'s' if hours > 1 else ''} {minutes} minute{'s' if minutes > 1 else ''}"

          if minutes > 0:
               return f"{minutes} minute{'s' if minutes > 1 else ''}"

          return "Less than a minute"

     @staticmethod
     def validate_image_file_extension(file_name):
          allowed_extensions = ("jpeg", "jpg", "png", "webp", "tiff", "avif")
          if not file_name.lower().endswith(allowed_extensions):
               raise DataBadRequestException(message=f'File extension should be in {allowed_extensions}.')
          return True

     @staticmethod
     def is_file_uploaded(img_file):
          """
          To check if there is a file uploaded in the server and check if it is a valid image file.
          :param img_file: To upload in server to check.
          :return: True, if there's a file and valid filename, otherwise False.
          """
          # set flag as not uploaded
          is_file_upload = False
          # check if there's file uploaded in server
          if img_file:
               # check if the size is valid
               if img_file.size > 0:
                    try:
                         # validate the image if
                         GlobalUtils.validate_image_file_extension(img_file.filename)
                         # then set flag as uploaded or True
                         is_file_upload = True
                    except Exception as e:
                         # raise error if not valid
                         raise e

          return is_file_upload

     @staticmethod
     def format_created_at(dt: datetime) -> str:
          if not dt:
               return None

          return dt.strftime("%A, %B %d, %Y")