# describe what service this app server is providing

from service.echo import ServiceEchoHandler
from service.pdf import ServicePdfHandler

domains = {
    'echo':{
        'handler':ServiceEchoHandler,
        'services':ServiceEchoHandler.services,
    },
    'pdf':{
        'handler':ServicePdfHandler,
        'services':ServicePdfHandler.services,
    },
}
