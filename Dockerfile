FROM maven:3.5.0-jdk-8-alpine
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

WORKDIR /tmp/src/
ADD . /tmp/src/

RUN mvn -P dockerbuild -s /usr/share/maven/ref/settings-docker.xml -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone,!orienteer-birt,!orienteer-bpm,!orienteer-camel,!orienteer-etl,!orienteer-taucharts,!orienteer-architect clean install && \
mkdir -p /app/runtime/ && \
mv orienteer-war/target/orienteer.war /app/ && \
mv target/jetty-runner.jar /app/ && \
cp orienteer.properties /app/ && \
rm -rf /tmp/src/ && \
ln -s /app/orienteer.war /app/active.war

WORKDIR /app/runtime/
VOLUME ["/app/runtime/"]
CMD ["java",  "-server", "-DORIENTDB_HOME=/app/runtime", "-Dorientdb.url=plocal:databases/Orienteer", "-Dorienteer.loader.repository.local=/root/.m2/repository", "-jar", "../jetty-runner.jar", "../active.war"]
