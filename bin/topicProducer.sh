#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/topicProducer/target/topicProducer.jar es.lab.activemq.jms.topic.TopicProducer $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/topicProducer/target/topicProducer.jar es.lab.activemq.jms.topic.TopicProducer $*

