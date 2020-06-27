#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqReplier/target/amqReplier.jar es.lab.activemq.amqreplier.AMQReplier $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqReplier/target/amqReplier.jar es.lab.activemq.amqreplier.AMQReplier $*

