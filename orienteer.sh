#!/usr/bin/env bash

mvn -P dockerbuild \
    -pl "!orienteer-archetype-war, !orienteer-archetype-jar, !orienteer-standalone,
    !orienteer-birt, !orienteer-bpm, !orienteer-camel, !orienteer-etl, !orienteer-taucharts,
    !orienteer-architect, !orienteer-mail, !orienteer-users, !orienteer-object" \
    clean install -DskipTests

if [ -z $NODE ]; then
    NODE="node_"$RANDOM
fi


if [ -z $PORT ]; then
    PORT=8080
fi

if [ -z $DISTRIBUTED ]; then
    DISTRIBUTED=false
fi

work_dir="Orienteer/$NODE/"
config_dir="Orienteer/$NODE/config"

mkdir -p $work_dir

cp orienteer-war/target/orienteer.war $work_dir
cp target/jetty-runner.jar $work_dir


if [ ! -d $config_dir ]; then
   cp -r orienteer-core/config $work_dir
fi

echo "Run Orienteer with node name \"$NODE\" on port \"$PORT\"."

cd $work_dir

java -server \
    -DORIENTDB_HOME=./ \
    -Dorientdb.url=plocal:databases/Orienteer \
    -Dorientdb.remote.url=remote:localhost/Orienteer \
    -Dorientdb.distributed=$DISTRIBUTED \
    -Dorientdb.node.name=$NODE \
    -Dorienteer.loader.repository.local=~/.m2/repository \
    -Xmx5120m \
    -XX:MaxDirectMemorySize=512g \
    -jar jetty-runner.jar --port $PORT orienteer.war
