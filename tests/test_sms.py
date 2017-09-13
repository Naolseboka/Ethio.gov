from src.sms import send_sms

def test_send_rejects_empty():
    assert send_sms('', 'hi') is False
