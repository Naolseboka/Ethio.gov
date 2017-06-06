import os
from src.services.citizens import lookup_citizen

def test_lookup():
    assert lookup_citizen('ET-1')['status'] == 'active'
