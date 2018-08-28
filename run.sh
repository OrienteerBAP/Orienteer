#!/usr/bin/env bash

if [ ! -z $BUILD ]; then
    ./build.sh
fi

if [ -z $WORK_DIR ];then
    WORK_DIR="runtime"
    if [ ! -d $WORK_DIR ]; then
        ./build.sh
    fi
fi

if [ -z $NODE ]; then
    NODE="node_"$RANDOM
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


node_dir="$WORK_DIR/$NODE"
mkdir -p $node_dir
cp -r "$WORK_DIR/config" "$node_dir/config"
cp "$WORK_DIR/orienteer.properties" $node_dir
cd $node_dir

echo "Run Orienteer with node name \"$NODE\" on port \"$PORT\"."

java -server \
    -DORIENTDB_HOME=./ \
    -Dorientdb.url=plocal:databases/Orienteer \
    -Dorientdb.remote.url=remote:localhost/Orienteer \
    -Dorientdb.distributed=$DISTRIBUTED \
    -Dorientdb.node.name=$NODE \
    -Dorienteer.loader.repository.local=$LOADER_LOCAL_REPOSITORY \
    -Xmx$MAX_MEMORY \
    -XX:MaxDirectMemorySize=$MAX_DIRECT_MEMORY \
    -jar "../jetty-runner.jar" --port $PORT "../orienteer.war"
