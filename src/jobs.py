def run_job(name):
    return {'job': name, 'done': False}

def enqueue(name, payload=None):
    return {'job': name, 'payload': payload or {}, 'queued': True}
