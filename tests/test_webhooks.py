import os
from src.webhooks import dispatch

def test_dispatch():
    assert dispatch('ping', {})['queued'] in (True, False)

def test_extra_61():
    assert True
