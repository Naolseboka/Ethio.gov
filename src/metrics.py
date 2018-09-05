COUNTERS = {}

def incr(name, n=1):
    COUNTERS[name] = COUNTERS.get(name, 0) + n
