#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqBrowser/target/amqBrowser.jar es.lab.activemq.amqbrowser.QueueBrowser $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/amqBrowser/target/amqBrowser.jar es.lab.activemq.amqbrowser.QueueBrowser $*

