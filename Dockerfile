FROM maven:3.5.0-jdk-8-alpine AS builder
WORKDIR /tmp/src/
ADD . /tmp/src/
RUN mvn -P dockerbuild -s /usr/share/maven/ref/settings-docker.xml -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone,!orienteer-birt,!orienteer-bpm,!orienteer-camel,!orienteer-etl,!orienteer-taucharts,!orienteer-architect clean install -DskipTests


FROM jetty:9.4-jre8
ENV ORIENTEER_HOME="/app"
ENV ORIENTDB_HOME="${ORIENTEER_HOME}/runtime"
ENV MVN_REPOSITORY="${ORIENTEER_HOME}/repository"
ENV JAVA_OPTIONS="$JAVA_OPTIONS -DORIENTEER_HOME=${ORIENTEER_HOME} -DORIENTDB_HOME=${ORIENTDB_HOME} -Dorientdb.url=plocal:${ORIENTDB_HOME}/databases/Orienteer -Dorienteer.loader.repository.local=${MVN_REPOSITORY}"
USER root
RUN mkdir -p ${ORIENTDB_HOME} && mkdir -p ${MVN_REPOSITORY} && chown -R jetty:jetty ${ORIENTEER_HOME}
COPY --from=builder /tmp/src/orienteer-war/target/orienteer.war /var/lib/jetty/webapps/ROOT.war
COPY --from=builder /tmp/src/orienteer.properties ${ORIENTEER_HOME}
USER jetty