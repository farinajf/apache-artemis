#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqRequester/target/amqRequester.jar es.lab.activemq.amqrequester.AMQRequester $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqRequester/target/amqRequester.jar es.lab.activemq.amqrequester.AMQRequester $*

