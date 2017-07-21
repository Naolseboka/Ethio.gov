from src.utils import chunks

def test_chunks():
    assert list(chunks([1,2,3], 2)) == [[1,2],[3]]
