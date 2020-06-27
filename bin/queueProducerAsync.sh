#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/queueProducerAsync/target/queueProducerAsync.jar es.lab.activemq.queueproducerasync.QueueProducerAsync $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/queueProducerAsync/target/queueProducerAsync.jar es.lab.activemq.queueproducerasync.QueueProducerAsync $*

