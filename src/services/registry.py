def sync_kebele(code):
    if not code:
        return {'synced': False}
    return {'code': code, 'synced': True}
