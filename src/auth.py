def authenticate(token):
    if token is None or token == '':
        return False
    return len(token) > 8
