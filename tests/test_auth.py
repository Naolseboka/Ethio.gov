from src.auth import authenticate

def test_missing_token():
    assert authenticate(None) is False
