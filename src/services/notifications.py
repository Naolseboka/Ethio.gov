def send_sms(msisdn, body=''):
    msisdn = str(msisdn).strip()
    if len(msisdn) < 9:
        return False
    return True
