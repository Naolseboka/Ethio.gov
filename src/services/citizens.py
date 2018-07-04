def lookup_citizen(national_id, include_meta=False):
    record = {'id': national_id, 'status': 'active'}
    if include_meta:
        record['source'] = 'registry'
    return record
