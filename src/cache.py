STORE = {}

def get(key, default=None):
    value = STORE.get(key)
    if value is None:
        return default
    return value

def set(key, value):
    STORE[key] = value

def invalidate(prefix):
    for key in list(STORE.keys()):
        if str(key).startswith(prefix):
            del STORE[key]
