import secrets
from datetime import datetime, timedelta, timezone

from fastapi import HTTPException
from fastapi.params import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.src.database.connection import create_session
from app.src.database.models import Visitor
from app.src.database.models.visitors_model import CreateVisitor
from app.src.validation.validate_fields import *


class RegisterVisitorController:

     @staticmethod
     async def register_visitor(register_visitor : CreateVisitor, db : AsyncSession  =  Depends(create_session)):
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
                       expiry_at=expiry_at
               )

               db.add(visitor)
               await db.flush()
               await db.refresh(visitor)

               return {
                       "ok"          : True,
                       "email": email,
                       "phone" : phone,
                       "purpose": register_visitor.purpose,
                       "qr_code"     : qr_code,
                       "host"     : register_visitor.host,
                       "notes"     : register_visitor.notes,
                       "expiry_at"   : expiry_at,
                       "visitor_id"  : visitor.visitor_id,
                       "visitor_name": full_name,
                       "current_time": datetime.now(timezone.utc)
               }

          except Exception as e:
               raise HTTPException(status_code=500, detail=str(e))
