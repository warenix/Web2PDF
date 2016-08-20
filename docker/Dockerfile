FROM ubuntu:16.04

# install necessary tools for setup
RUN apt-get update; apt-get install -y curl software-properties-common

# 0. start setup
WORKDIR /root

# 0. install dependency for wkhtml2pdf
RUN apt-get install -y fontconfig fontconfig-config fonts-dejavu-core \
libfontconfig1 libfontenc1 libfreetype6 libjpeg-turbo8 libx11-6 libx11-data \
libxau6 libxcb1 libxdmcp6 libxext6 libxfont1 libxrender1\
 x11-common xfonts-base xfonts-encodings xfonts-utils xfonts-75dpi 

# 0. fix chinese/japanese/hindi font problem
RUN apt-get install -y ttf-wqy-microhei fonts-indic

# 0. setup python-2.7
RUN apt-get -y install python-pip


RUN useradd -ms /bin/bash web2pdf

# 0. add app code
ADD Web2PDF-api /app
WORKDIR /app


RUN chown web2pdf:web2pdf /app
RUN python setup.py develop

EXPOSE 8080
USER web2pdf
ENTRYPOINT ["python", "app.py"]

