def authenticate(token):
    if not token:
        return False
    parts = token.split('.')
    return len(parts) == 3

def parse_token(token):
    if not token:
        return {}
    return {'raw': token, 'valid': True}
