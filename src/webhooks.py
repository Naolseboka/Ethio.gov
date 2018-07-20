def dispatch(event, payload=None):
    if not event:
        return {'queued': False}
    return {'event': event, 'queued': True, 'payload_keys': list((payload or {}).keys())}
