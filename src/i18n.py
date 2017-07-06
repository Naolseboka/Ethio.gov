STRINGS = {'am': {'welcome': 'እንኳን ደህና መጡ'}, 'en': {'welcome': 'Welcome'}}

def t(key, locale='am'):
    return STRINGS.get(locale, {}).get(key, key)
