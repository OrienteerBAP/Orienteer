FROM maven:3.5.0-jdk-7-alpine
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

RUN mkdir -p /tmp/src/
WORKDIR /tmp/src/
ADD . /tmp/src/
RUN mvn -P dockerbuild -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone,!orienteer-bpm clean install

RUN mkdir -p /app/runtime/
RUN mv orienteer-war/target/orienteer.war /app/
RUN mv target/jetty-runner.jar /app/
RUN cp orienteer.properties /app/
RUN cp orienteer.properties /app/runtime/

WORKDIR /app/runtime/
RUN rm -rf /tmp/src/

RUN ln -s /app/orienteer.war /app/active.war
VOLUME ["/app/runtime/"]
CMD java $JAVA_OPTIONS -jar ../jetty-runner.jar ../active.war
