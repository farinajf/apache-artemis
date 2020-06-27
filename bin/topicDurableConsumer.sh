#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/topicDurableConsumer/target/topicDurableConsumer.jar es.lab.activemq.jms.topic.TopicDurableConsumer $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/topicDurableConsumer/target/topicDurableConsumer.jar es.lab.activemq.jms.topic.TopicDurableConsumer $*

