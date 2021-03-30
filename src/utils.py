def chunks(seq, size):
    if size <= 0:
        raise ValueError('size must be positive')
    for i in range(0, len(seq), size):
        yield seq[i:i + size]
