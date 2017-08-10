STORE = {}

def get(key, default=None):
    if key in STORE:
        return STORE[key]
    return None

def set(key, value):
    if key is None:
        return False
    STORE[key] = value
    return True

def invalidate(prefix):
    for key in list(STORE.keys()):
        if str(key).startswith(prefix):
            del STORE[key]
