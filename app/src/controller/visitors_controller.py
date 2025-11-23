import secrets
from datetime import datetime, timedelta, timezone
from zoneinfo import ZoneInfo

from fastapi import HTTPException
from fastapi.encoders import jsonable_encoder
from fastapi.params import Depends
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlmodel import desc

from app.src.database.connection import create_session
from app.src.database.models import Logs, Visitor
from app.src.database.models.logs_model import Status
from app.src.database.models.visitors_model import CreateVisitor
from app.src.validation.validate_fields import *


class VisitorsController:

     @staticmethod
     async def register_visitor(register_visitor: CreateVisitor, db: AsyncSession = Depends(create_session)):
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
                       expiry_at=expiry_at,
               )

               db.add(visitor)
               await db.flush()
               await db.refresh(visitor)

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
             qr_code: str,
             db: AsyncSession = Depends(create_session)
     ):

          if not qr_code:
               raise HTTPException(status_code=400, detail="QR code is required")

          ph_tz = ZoneInfo("Asia/Manila")
          current_time_dt = datetime.now(ph_tz)
          current_time = current_time_dt.strftime("%Y-%m-%d %H:%M:%S")

          # -----------------------------------------
          # 1. GET VISITOR USING SQLMODEL ORM
          # -----------------------------------------
          query = select(Visitor).where(Visitor.qr_code == qr_code)
          result = await db.execute(query)
          visitor = result.first()

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

          db.add(visitor)
          await db.commit()
          await db.refresh(visitor)

          log = Logs(
                  visitor_id=visitor.visitor_id,
                  qr_code=visitor.qr_code,
                  status=Status.Valid,
                  timestamp=current_time_dt,
          )
          db.add(log)
          await db.commit()

          return {
                  "ok"          : True,
                  "msg"         : "Exit logged successfully! Thank you for visiting.",
                  "visitor_id"  : visitor.visitor_id,
                  "visitor_name": visitor.full_name,
                  "email"       : visitor.email,
                  "phone"       : visitor.phone,
                  "purpose"     : visitor.purpose,
                  "host"        : visitor.host,
                  "entry_time"  : VisitorsController.format_time(
                          visitor.created_at.strftime("%Y-%m-%d %H:%M:%S")),
                  "exit_time"   : VisitorsController.format_time(current_time),
                  "duration"    : VisitorsController.calculate_duration(
                          visitor.created_at.strftime("%Y-%m-%d %H:%M:%S"),
                          current_time,
                  )
          }

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
     async def get_visitors(db: AsyncSession = Depends(create_session)):
          try:

               query = select(
                       Visitor.visitor_id,
                       Visitor.full_name,
                       Visitor.email,
                       Visitor.purpose,
                       Visitor.host,
                       Visitor.qr_code,
                       Visitor.expiry_at,
                       Visitor.last_status,
                       Visitor.last_scan,
                       Visitor.created_at,
                       Visitor.phone,
                       Visitor.notes,
               ).order_by(desc(column=Visitor.visitor_id))
               result = await db.execute(query)
               visitors = result.mappings().all()

               return {
                       "ok"  : True,
                       "data": jsonable_encoder(visitors)
               }

          except Exception as e:
               raise HTTPException(status_code=500, detail=str(e))

     @staticmethod
     async def get_pagination_visitors(skip, limit, db: AsyncSession = Depends(create_session)):
          try:
               offset = (skip - 1) * limit
               query = select(Visitor.visitor_id,
                              Visitor.full_name,
                              Visitor.email,
                              Visitor.purpose,
                              Visitor.host,
                              Visitor.qr_code,
                              Visitor.expiry_at,
                              Visitor.last_status,
                              Visitor.last_scan,
                              Visitor.created_at,
                              Visitor.phone,
                              Visitor.notes,
                              ).limit(limit=limit).offset(offset=offset).order_by(desc(column=Visitor.visitor_id))
               result = await db.execute(query)
               visitors = result.mappings().all()

               return {
                       "ok"  : True,
                       "data": jsonable_encoder(visitors)
               }

          except Exception as e:
               raise HTTPException(status_code=500, detail=str(e))
