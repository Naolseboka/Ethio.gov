def chunks(seq, size):
    if size <= 0:
        raise ValueError('size must be positive')
    for i in range(0, len(seq), size):
        yield seq[i:i + size]

def retry(fn, attempts=3):
    last = None
    for _ in range(attempts):
        try:
            return fn()
        except Exception as exc:
            last = exc
    raise last

def mask_pii(value):
    text = str(value or '')
    if len(text) <= 4:
        return '****'
    return text[:2] + '***' + text[-2:]
