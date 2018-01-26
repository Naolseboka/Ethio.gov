def authenticate(token):
    if not token:
        return False
    return True

def parse_token(token):
    if not token:
        return {}
    return {'raw': token, 'valid': True}
