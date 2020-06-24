#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/queueConsumerTx/target/queueConsumerTx.jar es.lab.activemq.queueconsumertx.QueueConsumerTx $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/queueConsumerTx/target/queueConsumerTx.jar es.lab.activemq.queueconsumertx.QueueConsumerTx $*

