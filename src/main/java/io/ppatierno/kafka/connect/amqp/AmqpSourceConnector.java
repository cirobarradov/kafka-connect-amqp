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

package io.ppatierno.kafka.connect.amqp;

import io.ppatierno.kafka.connect.amqp.source.AmqpSourceConnectorConfig;
import io.ppatierno.kafka.connect.amqp.source.AmqpSourceTask;
import io.ppatierno.kafka.connect.amqp.util.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AmqpSourceConnector is a Kafka Connect connector that can receive messages
 * on AMQP protocol and deliver them into Kafka topic
 */
public class AmqpSourceConnector extends SourceConnector {
	
	private static final Logger LOG = LoggerFactory.getLogger(AmqpSourceConnector.class);

	private AmqpSourceConnectorConfig config;
	private Map<String, String> configProperties;
	
	@Override
	public void start(Map<String, String> props) {

		LOG.info("Start AMQP source connector");

		try {
			this.configProperties = props;
			this.config = new AmqpSourceConnectorConfig(props);
		} catch (ConfigException e) {
			throw new ConnectException("Couldn't start AmqpSourceConnector due to configuration error", e);
		}
	}

	@Override
	public void stop() {
		LOG.info("Stop AMQP source connector");
	}

	@Override
	public ConfigDef config() {
		return AmqpSourceConnectorConfig.CONFIG_DEF;
	}

	@Override
	public Class<? extends Task> taskClass() {
		return AmqpSourceTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int maxTasks) {

		// this method returns a list of configurations. The size of this list can't be greater than maxTasks
		// and it represents the number of tasks that will be instantiated

		// TODO : allow more than one AMQP server (one for task)

		LOG.info("AMQP source connector maxTasks = " + maxTasks);

		List<Map<String, String>> taskConfigs = new ArrayList<>(1);
		Map<String, String> taskProps = new HashMap<>(this.configProperties);
		taskConfigs.add(taskProps);
		return taskConfigs;
	}

	@Override
	public String version() {
		return Version.getVersion();
	}
	
	@Override
	public void initialize(ConnectorContext ctx) {
		super.initialize(ctx);
		LOG.info("AMQP source connector initialized");
	}
}
