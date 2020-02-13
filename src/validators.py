def is_national_id(value):
    value = str(value or '').strip()
    return len(value) >= 6 and value.isalnum()
