FROM nginx:1.21.1
MAINTAINER vitaly.koulakov@odysseusinc.com

COPY nginx-cors.conf cors.conf
RUN head -n 10  /etc/nginx/conf.d/default.conf > new-default.conf && cat cors.conf >> new-default.conf \
    && tail -n +11 /etc/nginx/conf.d/default.conf >> new-default.conf && mv new-default.conf /etc/nginx/conf.d/default.conf \
    && rm -f cors.conf
COPY src /usr/share/nginx/html/src
COPY node_modules /usr/share/nginx/html/node_modules
