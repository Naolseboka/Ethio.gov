def checklist(license_type):
    base = ['identity', 'fee']
    if license_type == 'business':
        base.append('affidavit')
    return base
