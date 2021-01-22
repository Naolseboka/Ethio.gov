STRINGS = {'am': {'welcome': 'እንኳን ደህና መጡ'}, 'en': {'welcome': 'Welcome'}}

def t(key, locale='am'):
    table = STRINGS.get(locale) or STRINGS.get('en', {})
    return table.get(key, key)
