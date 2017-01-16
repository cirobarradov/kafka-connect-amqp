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

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Configuration class for the AMQP sink connector
 */
public class AmqpSinkConnectorConfig extends AbstractConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpSinkConnectorConfig.class);

    // list of configuration parameters for each server
    List<Map<String, String>> configs;

    public static ConfigDef baseConfigDef() {
        return new ConfigDef();
    }

    public static final ConfigDef CONFIG_DEF = baseConfigDef();

    /**
     * Constructor
     *
     * @param props	properties map for configuration
     */
    public AmqpSinkConnectorConfig(Map<String, String> props) {

        super(CONFIG_DEF, props);
        LOG.info("Initialize AMQP sink Connector configuration");
    }
}
