FROM maven:3.5.0-jdk-7-alpine
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

WORKDIR /tmp/src/
ADD . /tmp/src/

RUN mvn -P dockerbuild -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone,!orienteer-bpm clean install && \
mkdir -p /app/runtime/ && \
mv orienteer-war/target/orienteer.war /app/ && \
mv target/jetty-runner.jar /app/ && \
cp orienteer.properties /app/ && \
cp orienteer.properties /app/runtime/ && \
rm -rf /tmp/src/ && \
ln -s /app/orienteer.war /app/active.war

WORKDIR /app/runtime/
VOLUME ["/app/runtime/"]
CMD sh -c "exec java -Dorienteer.loader.repository.local=$MAVEN_CONFIG/.m2/repository  $JAVA_OPTIONS -jar ../jetty-runner.jar ../active.war"
