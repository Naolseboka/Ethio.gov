STORE = {}

def get(key, default=None):
    if key in STORE:
        return STORE[key]
    return None

def set(key, value):
    STORE[key] = value
    return True
