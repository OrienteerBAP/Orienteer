FROM maven:latest
EXPOSE 8080
MAINTAINER Ilia Naryzhny (phantom@ydn.ru)

RUN mkdir -p /usr/src/orienteer/
WORKDIR /usr/src/orienteer/
ADD . /usr/src/orienteer/
RUN mvn -P dockerbuild -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone clean install

RUN mkdir -p /orienteer/
RUN mv orienteer-war/target/orienteer.war /orienteer/
RUN mv target/jetty-runner.jar /orienteer/
RUN cp orienteer.properties /orienteer/
RUN mvn clean


WORKDIR /orienteer/
RUN ln -s orienteer.war active.war
VOLUME ["/orienteer/Orienteer/"]
CMD ["java", "-jar", "jetty-runner.jar", "active.war"]

