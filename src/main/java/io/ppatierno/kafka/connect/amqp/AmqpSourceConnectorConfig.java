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

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Configuration class for the AMQP source connector
 */
public class AmqpSourceConnectorConfig extends AbstractConfig {

	private static final Logger LOG = LoggerFactory.getLogger(AmqpSourceConnectorConfig.class);
	
	// list of configuration parameters for each server
	List<Map<String, String>> configs;

	public static ConfigDef baseConfigDef() {
		return new ConfigDef()
				.define(AmqpSourceConnectorConstant.AMQP_SERVER_HOSTNAME, Type.STRING,
						AmqpSourceConnectorConstant.AMQP_SERVER_HOSTNAME_DEFAULT, Importance.HIGH,
						"AMQP server hostname")
				.define(AmqpSourceConnectorConstant.AMQP_SERVER_PORT, Type.INT,
						AmqpSourceConnectorConstant.AMQP_SERVER_PORT_DEFAULT, Importance.HIGH,
						"AMQP server port")
				.define(AmqpSourceConnectorConstant.AMQP_SERVER_CREDITS, Type.INT,
						AmqpSourceConnectorConstant.AMQP_SERVER_CREDITS_DEFAULT, Importance.HIGH,
						"AMQP credits for prefetch in flow control");
	}

	public static final ConfigDef CONFIG_DEF = baseConfigDef();

	/**
	 * Constructor
	 *
	 * @param props	properties map for configuration
     */
	public AmqpSourceConnectorConfig(Map<String, String> props) {

		super(CONFIG_DEF, props);
		LOG.info("Initialize AMQP source Connector configuration");
	}
}
