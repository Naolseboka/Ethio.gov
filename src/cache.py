STORE = {}

def get(key, default=None):
    value = STORE.get(key)
    if value is None:
        return default
    return value

def set(key, value):
    STORE[key] = value
