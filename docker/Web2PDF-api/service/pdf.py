import urllib, urllib2
from web import BaseServiceHandler
import json
import os, base64, hashlib
import pdfkit
import sys
import traceback
import urlparse
import time

class ServicePdfHandler(BaseServiceHandler):
    services = ['convert','remove', 'clean']

    def process_request(self, request, service):
        try:
            if service == 'convert':
                j = self.get_json_param(request)
                #url =  urllib.unquote(j['url'].decode('utf8'))
                #url = self.unquote_u(j['url'])
                url = j['url']
                url = url.encode('utf-8')

                default_orientation = 'Portrait'
                key = 'orientation'
                orientation = j[key] if key in j else default_orientation

                default_page_size = 'A4'
                key = 'page-size'
                page_size = j[key] if key in j else default_page_size


                aurl = 'http://zh.m.wikipedia.org/wiki/%E9%A6%99%E6%B8%AF'
                print 'url :', url
                print 'aurl:', aurl
                print 'equal?', url == aurl

                url_quote = url
                #url_quote = urllib2.quote(url)
                #url_quote = self.quote_u(url)
                filename=self.hash_url(url)

                options={
                        'orientation':orientation,
                        'page-size':page_size,
                        # setting dpi 75 to fix text spacing problem
                        # see: http://code.google.com/p/wkhtmltopdf/issues/detail?id=138#c13
                        'dpi':75,
                    }

                print 'options: %s' % options


                here = os.path.dirname(os.path.realpath(__file__))
                wkhtmltopdf_exe = '%s/../wkhtmltopdf' % here
                out_file='%s/../static/pdfout/%s.pdf' % (here, filename)
                print 'using wkhtmltopdf located at [%s]' % wkhtmltopdf_exe

                config = pdfkit.configuration(
                        wkhtmltopdf=wkhtmltopdf_exe
                        )

                try:
                    print 'converting [%s] now' % url_quote
                    print 'storing at [%s]' % out_file
                    pdfkit.from_url(url_quote, out_file, options=options, configuration=config)
                    print 'done'
                    parts = urlparse.urlparse(request.url)
                    pdf_local_url = ('%s://%s/pdfout/%s.pdf' % (
                        parts.scheme,
                        os.getenv('OPENSHIFT_APP_DNS', '%s' % (request.host)),
                        filename
                    ))
                    result = {
                            'url':url,
                            'pdf_url':pdf_local_url
                            }
                    self.set_result(result, 200)
                except:
                    e = sys.exc_info()[0]
                    print "Unexpected error", str(e)
                    traceback.print_exc()
                    self.set_result(str(e), 500)
            elif service == 'remove':
                j = self.get_json_param(request)
                filename = urllib.unquote(j['filename'])

                here = os.path.dirname(os.path.realpath(__file__))
                out_file='%s/../static/pdfout/%s' % (here, filename)
                print 'delete %s' % out_file
                os.remove(out_file)
                self.set_result(filename, 200)
            elif service == 'clean':
                keep_alive_sec = 15*60
                here = os.path.dirname(os.path.realpath(__file__))
                pdfout_dir ='%s/../static/pdfout' % (here)
                _dir = pdfout_dir
                files = (fle for rt, _, f in os.walk(_dir) for fle in f if time.time() - os.stat(
                        os.path.join(rt, fle)).st_mtime > keep_alive_sec )

                for f in files:
                    os.remove(os.path.join(pdfout_dir, f))

                self.set_result(True, 200)
        except:
            e = sys.exc_info()[0]
            print "Unexpected error", str(e)
            traceback.print_exc()
            self.set_result(str(e), 500)



    def hash_url(self, url):
        shorter_id = base64.urlsafe_b64encode(hashlib.md5(url).digest())[:11]
        return shorter_id

    def unquote_u(self, source):
        result = urllib.unquote(source)
        if '%u' in result:
            result = result.replace('%u','\\u').decode('unicode_escape')
        return result
    def quote_u(self, source):
        result = urllib.quote(source)
        if '%u' in result:
            result = result.replace('%u','\\u').decode('unicode_escape')
        return result

