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

RUN mkdir -p /var/local/shop/shop-shop

WORKDIR /var/local/shop/shop-web

# RUN activator clean compile dist && \
#  rm $APPDIR/target/universal/*.zip

EXPOSE 9000
EXPOSE 9999

ENTRYPOINT ["/usr/local/bin/activator"]

# CMD ["run"]

## To run image :
# docker run -ti --rm -p 49900:9000 \
#   flurdy/shop-web \
#   run
#
## Use local application folder :
# docker run -ti --rm -p 49900:9000 \
#   -v /path/to/myapplication:/var/local/shop/shop-web:rw \
#   flurdy/shop-web \
#   run
#
## Complex alternative :
# docker run -ti --rm -p 49900:9000 \
#   --volumes-from maven \
#   -v /path/to/myconffolder:/etc/opt/shop-web:r \
#   --link shop-service:shop-service \
#   flurdy/shop-web \
#   -Dconfig.file=/etc/opt/shop-web/my.conf
#
