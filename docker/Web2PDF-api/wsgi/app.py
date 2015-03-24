#!/usr/bin/python
from flask import Flask, request, jsonify, url_for, render_template
import settings

app = Flask(__name__, static_folder='static', static_url_path='')

@app.route('/')
def hello_world():
    ''' test'''
    if 'format' in request.args:
        if request.args['format'].lower() == 'json':
            return jsonify(ip='1234')
    return 'Hello World!'

@app.route('/<domain>/<service>', methods=['POST'])
def domain_service(domain, service):
    '''A domain provide certain services
content-type needs to be application/x-www-form-urlencoded
'''

    if domain in settings.domains.keys():
        service_list = settings.domains[domain]['services']

        if service in service_list:
            handler = settings.domains[domain]['handler']
            results, error_no = handler().handle_request(request, service)
            #if error_no != 200:
            #    return jsonify(error=results), error_no
            return results, error_no

    return jsonify(error='invalid domain service requested'), 404

@app.errorhandler(404)
@app.errorhandler(405)
def not_found(error):
        return jsonify(error=error.message)


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
