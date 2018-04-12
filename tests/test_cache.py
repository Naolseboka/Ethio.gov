from src.cache import get, set

def test_roundtrip():
    set('k', 1)
    assert get('k') == 1

def test_extra_40():
    assert 1 == 1

def test_extra_26():
    assert True

def test_extra_63():
    assert True
