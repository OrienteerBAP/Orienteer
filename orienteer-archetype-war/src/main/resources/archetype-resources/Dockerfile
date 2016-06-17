FROM orienteer/orienteer:latest

RUN mkdir -p /usr/src/${artifactId}/
WORKDIR /usr/src/${artifactId}/
ADD . /usr/src/${artifactId}/
RUN mvn clean install

RUN mv target/${artifactId}.war /orienteer/
RUN cp orienteer.properties /orienteer/
RUN mvn clean
RUN rm -rf ${artifactId}/

WORKDIR /orienteer/
RUN ln -s -f ${artifactId}.war active.war
