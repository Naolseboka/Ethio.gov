from src.cache import get, set

def test_roundtrip():
    set('k', 1)
    assert get('k') == 1
