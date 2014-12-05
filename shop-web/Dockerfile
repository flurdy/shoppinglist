FROM flurdy/activator:latest

MAINTAINER flurdy

ENV DEBIAN_FRONTEND noninteractive

ENV APPDIR /var/local/application

ADD repositories /root/.sbt/

ADD . /var/local/application

WORKDIR /var/local/application

RUN /usr/local/bin/activator stage

ENV APPNAME shop-web

ENTRYPOINT ["target/universal/stage/bin/$APPNAME"]

EXPOSE 9000
EXPOSE 9999
