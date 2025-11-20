import re
from typing import Dict, Any

def validate_full_name(name: str) -> Dict[str, Any]:
    if not name:
        return {"valid": False, "msg": "Name is required"}

    if len(name) < 2:
        return {"valid": False, "msg": "Name must be at least 2 characters"}

    name_parts = name.split()

    if len(name_parts) < 2:
        return {"valid": False, "msg": "Please enter both first name and last name"}

    for part in name_parts:
        if len(part) < 2:
            return {"valid": False, "msg": "Each name part must be at least 2 characters"}

    if not re.match(r"^[a-zA-Z\s\-'\.]+$", name):
        return {"valid": False, "msg": "Name can only contain letters, spaces, hyphens, and apostrophes"}

    return {"valid": True}


def validate_email(email: str) -> Dict[str, Any]:
    if not email:
        return {"valid": True}

    import re
    if not re.match(r"^[^@]+@[^@]+\.[^@]+$", email):
        return {"valid": False, "msg": "Invalid email format. Example: user@example.com"}

    common_typos = {
        "gmial.com": "gmail.com",
        "gmai.com": "gmail.com",
        "gmil.com": "gmail.com",
        "yaho.com": "yahoo.com",
        "yahooo.com": "yahoo.com",
        "hotmial.com": "hotmail.com",
        "outlok.com": "outlook.com",
    }

    local, _, domain = email.lower().partition("@")

    if domain in common_typos:
        return {"valid": False, "msg": f"Did you mean {common_typos[domain]}?"}

    if "." not in domain:
        return {"valid": False, "msg": "Email domain must include a dot (e.g., .com, .net)"}

    return {"valid": True}


def validate_ph_phone(phone: str) -> Dict[str, Any]:
    if not phone:
        return {"valid": True}

    phone_clean = re.sub(r"[\s\-\(\)]", "", phone)

    patterns = [
        r"^09\d{9}$",
        r"^639\d{9}$",
        r"^\+639\d{9}$",
    ]

    if not any(re.match(p, phone_clean) for p in patterns):
        return {"valid": False, "msg": "Invalid Philippine phone number. Use format: 09XXXXXXXXX"}

    return {"valid": True}

def sanitize_name(name: str) -> str:
    return name.title().strip()

def sanitize_email(email: str) -> str:
    return email.lower().strip() if email else email

def sanitize_phone(phone: str) -> str:
    return re.sub(r"[\s\-\(\)]", "", phone) if phone else phone
