FROM maven:3.5.0-jdk-8-alpine
EXPOSE 8080

WORKDIR /tmp/src/
ADD . /tmp/src/

ENV WORK_DIR /app/

VOLUME ["~/.m2/", "/root/.m2/"]

RUN ./build.sh \
    && cp ./run.sh /app/run.sh \
    && rm -rf /tmp/src/

WORKDIR /app/
VOLUME ["/app/"]

CMD ["./run.sh"]
