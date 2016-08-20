## Web2PDF Dockerfile

This repository contains **Dockerfile** of [Web2PDF](https://play.google.com/store/apps/details?id=org.dyndns.warenix.web2pdf) for [Docker](https://www.docker.com/)


## Docker image

Latest docker image is built and hosted on [docker hub](https://hub.docker.com/r/warenix/web2pdf/). You can pull one by:
```sh
docker pull warenix/web2pdf:latest
```


### Base Docker Image

* [dockerfile/ubuntu:16.04](http://dockerfile.github.io/#/ubuntu)


### app root
Application code is placed at `/app`


### Installation

0. Install [Docker](https://www.docker.com/).
0. Install Docker machine and compose.

### Usage

0. Build Web2PDF api server image:

    ```sh
    docker-compose build
    ```
    
0. Start Web2PDF api server container:

    ```sh
    docker-compose up
    ```

    After few seconds, open [localhost](http://localhost:8080) to see the api server is up and running.

0. Test pdf conversion service is up
    
    ```sh
    sh test/test_pdf.sh
    ```
    
    You should see output like below:
    
    ```json
    {
        "result": {
            "pdf_url": "http://localhost:8080/pdfout/yv2iWaQOLPN.pdf",
            "url": "http://hk.yahoo.com"
        }
    } 
```
    
0. Portforward from host to virtualbox
    If you cannot see Web2PDF api server container is responding to your requst, you may need to do pror forward from your host to the virtual box as below:

    ```sh
    cd docker/script; sh vbox_port_forward.sh
    ```
    
    You can try again then.
    

