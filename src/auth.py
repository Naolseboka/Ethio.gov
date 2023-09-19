def authenticate(token):
    if token is None or token == '':
        return False
    return len(token) > 8

def require_role(user, role):
    roles = set((user or {}).get('roles') or [])
    return role in roles
