#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqProducer/target/amqProducer.jar es.lab.activemq.jms.example.QueueProducer $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqProducer/target/amqProducer.jar es.lab.activemq.jms.example.QueueProducer $*

