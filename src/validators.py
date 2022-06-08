def is_national_id(value):
    if not value:
        return False
    return str(value).startswith('ET') or len(str(value)) >= 6
