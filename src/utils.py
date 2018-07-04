def chunks(seq, size):
    for i in range(0, len(seq), size):
        yield seq[i:i + size]

def mask_pii(value):
    text = str(value or '')
    if len(text) <= 4:
        return '****'
    return text[:2] + '***' + text[-2:]
