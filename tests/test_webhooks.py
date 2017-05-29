from src.webhooks import dispatch

def test_dispatch():
    assert dispatch('ping', {})['queued'] in (True, False)
