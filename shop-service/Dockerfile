FROM flurdy/activator-mini:latest

MAINTAINER flurdy

ENV DEBIAN_FRONTEND noninteractive

ENV GITHUBUSER flurdy
ENV APPBRANCH multipledawns
ENV APPNAME shoppinglist
ENV APPDIR /var/local/shop

# RUN wget https://github.com/$GITHUBUSER/$APPNAME/archive/$APPBRANCH.zip && \
#  unzip $APPBRANCH.zip && \
#  rm $APPBRANCH.zip && \
#  mv $APPNAME-$APPBRANCH $APPDIR

RUN mkdir -p /var/local/shop/shop-service

WORKDIR /var/local/shop/shop-service

# RUN activator clean compile dist && \
#  rm $APPDIR/target/universal/*.zip

EXPOSE 8880

ENTRYPOINT ["/usr/local/bin/activator"]

# CMD ["run"]

## To run image :
# docker run -ti --rm -P \
#   flurdy/shop-service \
#   run
#
## Use local application folder :
# docker run -ti --rm -P \
#   -v /path/to/myapplication:/var/local/shop/shop-service:rw \
#   --volumes-from maven \
#   --name shop-service \
#   flurdy/shop-service
#
## Complex alternative :
# docker run -ti --rm -P \
#   -v /path/to/myconffolder:/etc/opt/shop-service:r \
#   --volumes-from maven \
#   --name shop-service \
#   flurdy/shop-service \
#   -Dconfig.file=/etc/opt/shop-service/my.conf
#
#
