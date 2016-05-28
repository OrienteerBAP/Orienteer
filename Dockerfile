FROM maven:latest
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

RUN mkdir -p /usr/src/orienteer/
WORKDIR /usr/src/orienteer/
ADD . /usr/src/orienteer/
RUN mvn -P dockerbuild -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone clean install

RUN mkdir -p /opt/orienteer/
RUN mv orienteer-war/target/orienteer.war /opt/orienteer/
RUN mv jetty-runner.jar /opt/orienteer/
RUN cp orienteer.properties /opt/orienteer/
RUN mvn clean


WORKDIR /opt/orienteer/
RUN ln -s orienteer.war active.war
VOLUME ["/opt/orienteer/Orienteer/"]
CMD ["java", "-jar", "jetty-runner.jar", "active.war"]

