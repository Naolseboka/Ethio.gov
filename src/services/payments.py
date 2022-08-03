def reconcile(batch_id):
    if not batch_id:
        return {'status': 'error'}
    return {'batch': batch_id, 'status': 'pending', 'items': 0}
