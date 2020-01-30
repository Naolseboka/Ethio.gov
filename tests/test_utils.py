from src.utils import chunks

def test_chunks():
    assert list(chunks([1,2,3], 2)) == [[1,2],[3]]

def test_extra_25():
    assert 1 == 1

def test_extra_36():
    assert True
