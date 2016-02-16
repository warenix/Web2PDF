import urllib, urllib2
from web import BaseServiceHandler
import json

class ServiceEchoHandler(BaseServiceHandler):
    services = ['echo', 'json']

    def process_request(self, request, service):
        if service == 'echo':
            if self.validate_required_args(request, ['message']):
                message = request.form['message']
                message = urllib.unquote(message)
                self.set_result(message, 200)
            else:
                self.set_result('invalid arguments', 400)
        elif service == 'json':
            j = self.get_json_param(request)
            self.set_result(str(j), 200)

