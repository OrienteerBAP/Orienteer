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

#mvn -P dockerbuild \
#    $docker_settings \
#    -pl "!orienteer-archetype-war, !orienteer-archetype-jar, !orienteer-standalone,
#    !orienteer-birt, !orienteer-bpm, !orienteer-camel, !orienteer-etl, !orienteer-taucharts,
#    !orienteer-architect, !orienteer-mail, !orienteer-users, !orienteer-object" \
#    clean install $tests



config_dir="$WORK_DIR/config"

mkdir -p $WORK_DIR
cd $WORK_DIR

if [ ! -d "jetty" ]; then
    unzip ../target/jetty-distribution.zip -d .
    mv "jetty-distribution-9.4.12.v20180830" "jetty"

    java -jar "jetty/start.jar" jetty.home="jetty" jetty.base="jetty" --create-startd

    echo "--module=session-store-hazelcast-remote
    jetty.session.hazelcast.mapName=jetty-distributed-session-map
    jetty.session.hazelcast.hazelcastInstanceName=orienteer-hazelcast
    jetty.session.hazelcast.configurationLocation=../../
    jetty.session.hazelcast.onlyClient=false
    jetty.session.gracePeriod.seconds=3600
    jetty.session.savePeriod.seconds=0" > "jetty/start.d/session-store-hazelcast-embedded.ini"

    mkdir -p jetty/lib/hazelcast

    cp ~/.m2/repository/com/hazelcast/hazelcast/3.9.4/hazelcast-3.9.4.jar jetty/lib/hazelcast
fi

pwd
cp ../orienteer-war/target/orienteer.war "orienteer.war"
cp ../orienteer.properties .
cp -r ../orienteer-core/config .