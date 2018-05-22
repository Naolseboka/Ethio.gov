def is_national_id(value):
    return bool(value) and len(value) >= 6

def validate_msisdn(msisdn):
    digits = ''.join(ch for ch in str(msisdn) if ch.isdigit())
    return len(digits) >= 9
