def route(path, handler, methods=None):
    return {'path': path, 'handler': handler, 'methods': methods or ['GET']}
