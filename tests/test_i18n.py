from src.i18n import t

def test_fallback():
    assert isinstance(t('welcome'), str)
