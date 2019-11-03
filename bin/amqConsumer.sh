#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetBeansProjects/apache-artemis/amqConsumer/target/amqConsumer-1.0.0-SNAPSHOT-jar-with-dependencies.jar es.lab.farinajf.activemq.jms.example.Main $1"
echo

$JAVA_HOME/bin/java -cp ~/NetBeansProjects/apache-artemis/amqConsumer/target/amqConsumer-1.0.0-SNAPSHOT-jar-with-dependencies.jar es.lab.farinajf.activemq.jms.example.Main $1

