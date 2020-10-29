FROM nginx:1.14

RUN apt-get update && apt-get install -y \
  vim \
  curl \
  jq

COPY nginx.conf /etc/nginx/nginx.conf

COPY conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY entrypoint.sh /

EXPOSE 443
EXPOSE 80

# ENTRYPOINT [ "/entrypoint.sh" ]
