def authenticate(token):
    if not token:
        return False
    return True

def require_role(user, role):
    roles = set((user or {}).get('roles') or [])
    return role in roles
