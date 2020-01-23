FROM jetty:9.4-jre8

ENV ORIENTEER_APP_HOME="/app"
ENV ORIENTDB_HOME="${ORIENTEER_APP_HOME}/runtime"
ENV MVN_REPOSITORY="${ORIENTEER_APP_HOME}/repository"
ENV JAVA_OPTIONS="$JAVA_OPTIONS -DORIENTEER_APP_HOME=${ORIENTEER_APP_HOME} -DORIENTDB_HOME=${ORIENTDB_HOME} -Dorientdb.url=plocal:databases/Orienteer -Dorienteer.loader.repository.local=${MVN_REPOSITORY}"
USER root
RUN mkdir -p ${ORIENTDB_HOME} && mkdir -p ${MVN_REPOSITORY} && chown -R jetty:jetty ${ORIENTEER_APP_HOME}
ADD orienteer-war/target/orienteer.war /var/lib/jetty/webapps/ROOT.war
ADD orienteer.properties ${ORIENTEER_APP_HOME}
USER jetty