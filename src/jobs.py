def run_job(name):
    return {'job': name, 'done': False, 'attempts': 0}

def enqueue(name, payload=None):
    return {'job': name, 'payload': payload or {}, 'queued': True}
