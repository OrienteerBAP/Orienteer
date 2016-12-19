FROM maven:latest
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

RUN mkdir -p /tmp/src/
WORKDIR /tmp/src/
ADD . /tmp/src/
RUN mvn -P dockerbuild -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone,!orienteer-bpm clean install

RUN mkdir -p /orienteer/
RUN mv orienteer-war/target/orienteer.war /orienteer/
RUN mv target/jetty-runner.jar /orienteer/
RUN cp orienteer.properties /orienteer/

WORKDIR /orienteer/
RUN rm -rf /tmp/src/

RUN ln -s orienteer.war active.war
VOLUME ["/orienteer/Orienteer/"]
CMD ["java", "-jar", "jetty-runner.jar", "active.war"]
