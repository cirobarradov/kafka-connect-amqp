/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ppatierno.kafka.connect.amqp.sink;

import io.ppatierno.kafka.connect.amqp.util.Version;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonSender;
import io.vertx.proton.ProtonServer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AmqpSinkTask is a Kafka Connect task that gets messages
 * from Kafka and sends them through the AMQP protocol
 */
public class AmqpSinkTask extends SinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpSinkTask.class);

    private String serverHostname;
    private int serverPort;

    private Map<String, List<ProtonSender>> senders;

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {

        LOG.info("Start AMQP sink task");

        this.senders = new HashMap<>();

        this.serverHostname = props.get(AmqpSinkConnectorConstant.AMQP_SERVER_HOSTNAME);
        this.serverPort = Integer.valueOf(props.get(AmqpSinkConnectorConstant.AMQP_SERVER_PORT));

        Vertx vertx = Vertx.vertx();

        ProtonServer server = ProtonServer.create(vertx)
                .connectHandler((connection) -> {
                    processConnection(connection);
                })
                .listen(this.serverPort, this.serverHostname, done -> {

                    if (done.succeeded()) {
                        LOG.info("Listening on {}", done.result().actualPort());
                    } else {
                        done.cause().printStackTrace();
                    }
                });
    }

    @Override
    public void put(Collection<SinkRecord> records) {

        if (records.isEmpty()) {
            return;
        }

        for (SinkRecord record: records) {

            List<ProtonSender> senders = this.senders.get(record.topic());
            if (senders != null) {

                for (ProtonSender sender: senders) {

                    Message message = Proton.message();
                    message.setAddress(record.topic());

                    // just handling string content
                    Schema.Type t = record.valueSchema().type();
                    switch (t) {
                        case STRING:

                            String value = (String)record.value();
                            message.setBody(new Data(new Binary(value.getBytes())));

                            break;
                    }
                    sender.send(message);
                }
            }
        }
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        LOG.info("flush");
    }

    @Override
    public void stop() {
        LOG.info("Stop AMQP sink connector");
    }

    private void processConnection(ProtonConnection connection) {

        connection.openHandler(done ->{

            LOG.info("Client connected: {}", done.result().getRemoteContainer());

        }).closeHandler(done -> {

            LOG.info("Client closing connection: {}", done.result().getRemoteContainer());
            done.result().close();
            done.result().disconnect();

        }).disconnectHandler(conn -> {

            LOG.info("Client disconnected: {}", conn.getRemoteContainer());
            conn.disconnect();

        }).open();

        connection.sessionOpenHandler(session -> session.open());

        connection.senderOpenHandler(sender -> {

            sender.setSource(sender.getRemoteSource());

            if (!this.senders.containsKey(sender.getSource())) {
                this.senders.put(sender.getSource().getAddress(), new ArrayList<>());
            }

            this.senders.get(sender.getSource().getAddress()).add(sender);

            sender.open();

        }).open();

    }
}
