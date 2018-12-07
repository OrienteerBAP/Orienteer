#!/usr/bin/env bash

if [ -z $WORK_DIR ]; then
    WORK_DIR="app/"
fi

if [ "$SKIP_TESTS" = true ]; then
    tests="-DskipTests"
fi

if [ -f "/usr/share/maven/ref/settings-docker.xml" ]; then
    docker_settings="-s /usr/share/maven/ref/settings-docker.xml"
fi

mvn -P dockerbuild \
    $docker_settings \
    -pl "!orienteer-archetype-war, !orienteer-archetype-jar, !orienteer-standalone,
    !orienteer-birt, !orienteer-bpm, !orienteer-camel, !orienteer-etl, !orienteer-taucharts,
    !orienteer-architect, !orienteer-mail, !orienteer-users, !orienteer-object" \
    clean install $tests




mkdir -p $WORK_DIR
cd $WORK_DIR

mkdir -p jetty/lib

cp ../target/jetty-runner.jar     jetty/
cp ../orienteer-war/src/main/webapp/WEB-INF/jetty.xml jetty/
cp ../target/jetty-hazelcast.jar  jetty/lib
cp ../target/hazelcast.jar        jetty/lib
cp ../target/hazelcast-client.jar jetty/lib

cp ../orienteer-war/target/orienteer.war .
cp ../orienteer.properties orienteer-default.properties
cp -r ../orienteer-core/config config-default