from src.services.payments import reconcile

def test_reconcile():
    assert reconcile('b1')['status'] in ('pending', 'error')
