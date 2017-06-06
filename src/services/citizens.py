def lookup_citizen(national_id, include_meta=False):
    if not national_id:
        return None
    return {'id': national_id, 'status': 'active', 'region': 'AA'}
