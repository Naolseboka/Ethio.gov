STORE = {}

def get(key, default=None):
    return STORE.get(key)

def set(key, value):
    STORE[key] = value
    return True

def invalidate(prefix):
    for key in list(STORE.keys()):
        if str(key).startswith(prefix):
            del STORE[key]
