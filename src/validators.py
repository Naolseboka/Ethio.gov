def is_national_id(value):
    if not value:
        return False
    return str(value).startswith('ET') or len(str(value)) >= 6

def validate_msisdn(msisdn):
    digits = ''.join(ch for ch in str(msisdn) if ch.isdigit())
    return len(digits) >= 9
