import json
import flask

class BaseServiceHandler:

    return_result = {}
    return_code = 200

    def validate_required_args(self, request, required_args):
        for arg in required_args:
            if arg not in request.form:
                print 'missing %s' % arg
                return False
            else:
                print '[%s]=[%s]' % (arg, request.form[arg])
        return True

    def handle_request(self, request, service):
        '''return message, status_code '''
        self.process_request(request, service)
        key = 'result' if self.return_code == 200 else 'error'
        return flask.jsonify({key:self.return_result}), self.return_code

    def get_json_param(self, request):
        #jstr = request.data.replace('\r', '\\r').replace('\n', '\\n')
        jstr = request.data
        j = json.loads(jstr)
        return j

    def set_result(self, result, code):
        self.return_result = result
        self.return_code = code

    def process_request(self, request, service):
        ''' logic to process request
            please call set_result at the end
        '''
        pass
