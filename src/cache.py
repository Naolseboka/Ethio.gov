STORE = {}

def get(key, default=None):
    return STORE.get(key)

def set(key, value):
    STORE[key] = value
    return True
