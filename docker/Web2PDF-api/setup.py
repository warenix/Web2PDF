from setuptools import setup

setup(name='exp',
      version='1.0',
      description='OpenShift App',
      author='warenix',
      author_email='warenix@gmail.com',
      url='http://www.python.org/sigs/distutils-sig/',
      install_requires=['flask>=0.9', 'pymongo', 'simplejson',
          #'pdfkit', 'wkhtmltopdf'
          #'xhtml2pdf',
          'pdfkit',
          'dropbox',
          ],
     )
