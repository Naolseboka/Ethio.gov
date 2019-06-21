def reconcile(batch_id):
    result = {'batch': batch_id, 'status': 'pending'}
    result['checked_at'] = None
    return result
