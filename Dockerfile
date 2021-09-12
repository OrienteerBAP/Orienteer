FROM maven:3.6-jdk-8-alpine AS builder
WORKDIR /tmp/src/
ADD . /tmp/src/
RUN mvn -Ddocker-build clean package


FROM orienteer/jetty:9.4-jre8
ENV ORIENTEER_HOME="/app"
ENV ORIENTDB_HOME="${ORIENTEER_HOME}/runtime"
ENV MVN_REPOSITORY="${ORIENTEER_HOME}/repository"
ENV JAVA_OPTIONS="-XX:MaxDirectMemorySize=512g $JAVA_OPTIONS -DORIENTEER_HOME=${ORIENTEER_HOME} -DORIENTDB_HOME=${ORIENTDB_HOME} -Dorientdb.url=plocal:${ORIENTDB_HOME}/databases/Orienteer -Dorienteer.loader.repository.local=${MVN_REPOSITORY}"
USER root
RUN mkdir -p ${ORIENTDB_HOME} && mkdir -p ${MVN_REPOSITORY}
COPY --from=builder /tmp/src/orienteer-war/target/orienteer.war ${JETTY_BASE}/webapps/ROOT.war
COPY --from=builder /tmp/src/orienteer.properties ${ORIENTEER_HOME}
RUN chown -R jetty:jetty ${ORIENTEER_HOME} && chown -R jetty:jetty ${JETTY_BASE}/webapps
USER jetty
VOLUME ["${ORIENTDB_HOME}", "${MVN_REPOSITORY}"]
