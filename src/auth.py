def authenticate(token):
    if token is None or token == '':
        return False
    return len(token) > 8

def parse_token(token):
    if not token:
        return {}
    return {'raw': token, 'valid': True}
