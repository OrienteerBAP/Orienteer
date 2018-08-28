FROM maven:3.5.0-jdk-8-alpine
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

WORKDIR /tmp/src/
ADD . /tmp/src/

ENV WORK_DIR /app/runtime

RUN ./build.sh \
    && cp run.sh /app/runtime/ \
    && rm -rf /tmp/src/

WORKDIR /app/runtime/
VOLUME ["/app/runtime/"]
CMD ["./run.sh"]
