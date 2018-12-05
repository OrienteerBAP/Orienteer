#!/usr/bin/env bash

if [ ! -z $BUILD ]; then
    ./build.sh
fi

if [ -z $WORK_DIR ];then
    WORK_DIR="app"
    if [ ! -d $WORK_DIR ]; then
        ./build.sh
    fi
fi

if [ -z $NODE ]; then
    NODE="node_$HOSTNAME"
fi

if [ -z $PORT ]; then
    PORT=8080
fi

if [ -z $DISTRIBUTED ]; then
    DISTRIBUTED=false
fi

if [ -z $MAX_MEMORY ]; then
    if [ $DISTRIBUTED ];
    then
        MAX_MEMORY="4096m"
    else
        MAX_MEMORY="2048m"
    fi
fi

if [ -z $MAX_DIRECT_MEMORY ]; then
    MAX_DIRECT_MEMORY="512g"
fi

if [ -z $LOADER_LOCAL_REPOSITORY ]; then
    LOADER_LOCAL_REPOSITORY="~/.m2/repository"
fi

if [ "$DOCKER_SWARM" = true ];
then
    if [ -z $HAZELCAST_PUBLIC_ADRESS ]; then
        HAZELCAST_PUBLIC_ADRESS="$HOSTNAME:5701"
    fi
    JAVA_OPTS="$JAVA_OPTS -Dorientdb.ip.address=$HOSTNAME -Dhazelcast.local.publicAddress=$HAZELCAST_PUBLIC_ADRESS"
else
    if [ ! -z $HAZELCAST_PUBLIC_ADRESS ]; then
        JAVA_OPTS="$JAVA_OPTS -Dhazelcast.local.publicAddress=$HAZELCAST_PUBLIC_ADRESS"
    fi
    if [ ! -z $ORIENTDB_PUBLIC_ADDRESS ]; then
        JAVA_OPTS="$JAVA_OPTS -Dorientdb.ip.address=$ORIENTDB_PUBLIC_ADDRESS"
    fi
fi

cd $WORK_DIR

node_dir="runtime/$NODE"
mkdir -p $node_dir
cp orienteer.war "jetty/webapps/$NODE.war"

java -server \
    -DORIENTDB_HOME=$node_dir \
    -Dorientdb.url="plocal:$node_dir/databases/Orienteer" \
    -Dorientdb.remote.url=remote:localhost/Orienteer \
    -Dorientdb.distributed=$DISTRIBUTED \
    -Dorientdb.node.name=$NODE \
    -Dorienteer.loader.repository.local=$LOADER_LOCAL_REPOSITORY \
    $JAVA_OPTS \
    -Xmx$MAX_MEMORY \
    -XX:MaxDirectMemorySize=$MAX_DIRECT_MEMORY \
    -Djetty.port=$PORT \
    -jar jetty/start.jar jetty.home="jetty" jetty.base="jetty"
