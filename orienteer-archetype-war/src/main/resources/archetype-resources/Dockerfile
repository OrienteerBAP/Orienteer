FROM orienteer/orienteer:latest

RUN mkdir -p /tmp/src/
WORKDIR /tmp/src/
ADD . /tmp/src/
RUN mvn clean install

RUN mv target/${artifactId}.war /orienteer/
RUN cp orienteer.properties /orienteer/


WORKDIR /orienteer/
RUN rm -rf /tmp/src/
RUN ln -s -f ${artifactId}.war active.war
