FROM maven:3.6-jdk-8-alpine AS builder
WORKDIR /tmp/src/
ADD . /tmp/src/
RUN mvn -Ddocker-build clean package


FROM orienteer/orienteer:latest
COPY --from=builder /tmp/src/target/${artifactId}.war ${JETTY_BASE}/webapps/ROOT.war
COPY --from=builder /tmp/src/orienteer.properties ${ORIENTEER_HOME}
