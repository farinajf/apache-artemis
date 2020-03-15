#!/bin/bash

echo "java -jar target/topicProducer.jar topic.t1 $1 1000 u1 u1"
echo

java -jar target/topicProducer.jar topic.t1 $1 1000 u1 u1

