# AMQP Kafka Connect

An AMQP connector for data ingestion into Kafka via AMQP protocol.

# Development

This connector is under development and in the status of a POC, so more changes will happen over time.

In order to use it, the packaging is needed with following Maven command :

    mvn package

After that, from the Apache Kafka installation directory, it's needed to put the packaged JAR into the Java classpath with
following command :

    export CLASSPATH=/<path-to-repo>/kafka-connect-amqp/target/kafka-connect-amqp-0.0.1-SNAPSHOT-jar-with-dependencies.jar

For the AMQP source connector, it's possible to start the Kafka Connect framework in the following way :

    bin/connect-standalone.sh /<path-to-repo>/kafka-connect-amqp/connect-standalone.properties /<path-to-repo>/kafka-connect-amqp/config/amqp-source.properties

Instead for the AMQP sink connector :

    bin/connect-standalone.sh /<path-to-repo>/kafka-connect-amqp/connect-standalone.properties /<path-to-repo>/kafka-connect-amqp/config/amqp-sink.properties

Of course, it's possible to start the AMQP source and sink connectors together passing both the properties files on the command line.

## Debugging

In order to enable the connector debugging, a "remote" session should be configured in the IDE (i.e. IntelliJ or Eclipse) in order to attach
to an already running JVM instance.
Before starting the Kafka Connect framework as before, the _KAFKA_DEBUG_ environment variable needs to be exported :

    export KAFKA_DEBUG=y

For debugging the connector from the very early stage (creation and initialization), the _DEBUG_SUSPEND_FLAG_ environment variable is
needed, in order to have the JVM instance started in a suspended mode until the "remote" session from the IDE is attached on that.

    export DEBUG_SUSPEND_FLAG=y