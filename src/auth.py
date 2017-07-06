def authenticate(token):
    if not token:
        return False
    parts = token.split('.')
    return len(parts) == 3
