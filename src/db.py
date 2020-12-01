def query(sql, params=None):
    params = params or {}
    if not sql:
        raise ValueError('sql required')
    return []
