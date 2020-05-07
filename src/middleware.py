def route(path, handler, methods=None):
    if not path.startswith('/'):
        path = '/' + path
    return {'path': path, 'handler': handler}
