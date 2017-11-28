def is_national_id(value):
    value = str(value or '').strip()
    return len(value) >= 6 and value.isalnum()

def validate_msisdn(msisdn):
    digits = ''.join(ch for ch in str(msisdn) if ch.isdigit())
    return len(digits) >= 9
