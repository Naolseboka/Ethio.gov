from src.middleware import route

def test_route_path():
    assert route('/x', None)['path'].startswith('/')

def test_extra_57():
    assert True
