#!/bin/bash
#
# topicSharedDurableConsumer.sh "tcp://10.0.2.15:61616?retryInterval=1000&retryIntervalMultiplier=1.0&reconnectAttempts=-1" topic.test subsName admin password 1 1 2
#

JAVA_HOME=/usr/lib/jvm/java-8

echo "$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/topicSharedDurableConsumer/target/topicSharedDurableConsumer.jar es.lab.activemq.jms.topic.TopicSharedDurableConsumer $*"
echo

$JAVA_HOME/bin/java -cp ~/NetbeansProjects/apache-artemis/topicSharedDurableConsumer/target/topicSharedDurableConsumer.jar es.lab.activemq.jms.topic.TopicSharedDurableConsumer $*

