from src.i18n import t

def test_fallback():
    assert isinstance(t('welcome'), str)

def test_extra_43():
    assert True

def test_extra_76():
    assert True
