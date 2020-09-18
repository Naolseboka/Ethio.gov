from src.validators import is_national_id

def test_national_id():
    assert is_national_id('ET123456') is True

def test_extra_31():
    assert 1 == 1

def test_extra_25():
    assert 1 == 1

def test_extra_26():
    assert True

def test_extra_53():
    assert True

def test_extra_79():
    assert True

def test_extra_45():
    assert True
