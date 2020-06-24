#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/queueProducerTx/target/queueProducerTx.jar es.lab.activemq.queueproducertx.QueueProcuderTx $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/queueProducerTx/target/queueProducerTx.jar es.lab.activemq.queueproducertx.QueueProcuderTx $*

