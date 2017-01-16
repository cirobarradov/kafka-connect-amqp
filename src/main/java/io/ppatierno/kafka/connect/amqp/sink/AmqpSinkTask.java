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
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * AmqpSinkTask is a Kafka Connect task that gets messages
 * from Kafka and sends them through the AMQP protocol
 */
public class AmqpSinkTask extends SinkTask {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpSinkTask.class);

    @Override
    public String version() {
        return Version.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {

        LOG.info("Start AMQP sink task");
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        LOG.info("put");
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> map) {
        LOG.info("flush");
    }

    @Override
    public void stop() {
        LOG.info("Stop AMQP sink connector");
    }
}
