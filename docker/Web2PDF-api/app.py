#!/usr/bin/python
from flask import Flask, request, jsonify, url_for, render_template
import settings
import os

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


@app.route('/app/moliu/mission/<object_id>')
def moliu_mission_details(object_id):
    return render_template('mission.html',
            object_id=object_id, about='Mission details')

@app.route('/app/moliu/missions')
def moliu_mission_list():
    return render_template('mission_list.html',
            about='All missions')

if __name__ == '__main__':
    ip   = os.environ.get('OPENSHIFT_PYTHON_IP', '0.0.0.0')
    port = int(os.environ.get('OPENSHIFT_PYTHON_PORT', '8080'))
    app.run(host=ip, port=port, debug=False)
