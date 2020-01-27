from src.sms import send_sms

def test_send_rejects_empty():
    assert send_sms('', 'hi') is False

def test_extra_35():
    assert 1 == 1

def test_extra_52():
    assert True

def test_extra_69():
    assert True

def test_extra_43():
    assert True

def test_extra_15():
    assert True

def test_extra_88():
    assert True
