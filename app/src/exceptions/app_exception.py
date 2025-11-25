from fastapi import status


class BaseAppException(Exception):
     def __init__(self, message_status='error', message='Internal server error. Contact developer if the problem persists.',
                  status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,headers = None):
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code,headers)


class DataBaseDataNotFoundException(BaseAppException):
     def __init__(self, message_status='fail', message='The requested data was not found.',
                  status_code=status.HTTP_404_NOT_FOUND,headers = None):
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code,headers)


class JWTExpiredException(BaseAppException):
     def __init__(self, message_status='fail', message='Your session has expired. Please log in again.',
                  status_code=status.HTTP_401_UNAUTHORIZED,headers = None):
          if headers is None:
               headers = {'WWW-Authenticate': 'Bearer'}
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code,headers)

class JWTInvalidException(BaseAppException):
     def __init__(self, message_status='fail', message='Invalid token, please login again.',
                  status_code=status.HTTP_401_UNAUTHORIZED, headers=None):
          if headers is None:
               headers = {'WWW-Authenticate': 'Bearer'}
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code,headers)

class DataForbiddenException(BaseAppException):
     def __init__(self, message_status='fail', message="Access forbidden. You don’t have permission to perform this action.",
                  status_code=status.HTTP_403_FORBIDDEN,headers = None):
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code,headers)


class UnAuthorizeAccessException(BaseAppException):
     def __init__(self, message_status='fail', message="You are not authorized to access this resource. ",
                  status_code=status.HTTP_401_UNAUTHORIZED,headers = None):
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code,headers)

class DataBadRequestException(BaseAppException):
     def __init__(self, message_status='fail', message="Bad request.",
                  status_code=status.HTTP_400_BAD_REQUEST, headers=None):
          self.message_status = message_status
          self.headers = headers
          self.message = message
          self.status_code = status_code
          super().__init__(message_status, message, status_code, headers)