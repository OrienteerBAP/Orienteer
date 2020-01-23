#!/bin/sh

mvn -pl !orienteer-archetype-war,!orienteer-archetype-jar,!orienteer-standalone,!orienteer-birt,!orienteer-bpm,!orienteer-camel,!orienteer-etl,!orienteer-taucharts,!orienteer-architect clean install
docker build .