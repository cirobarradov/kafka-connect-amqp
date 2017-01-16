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

/**
 * AMQP source connector constants
 */
public class AmqpSourceConnectorConstant {

	// configuration default values
	public static final String AMQP_SERVER_HOSTNAME_DEFAULT = "localhost";
	public static final int AMQP_SERVER_PORT_DEFAULT = 5672;
	public static final int AMQP_SERVER_CREDITS_DEFAULT = 100;

	// configuration keys
	public static final String AMQP_SERVER_HOSTNAME = "amqp.server.hostname";
	public static final String AMQP_SERVER_PORT = "amqp.server.port";
	public static final String AMQP_SERVER_CREDITS = "amqp.server.credits";

	public static final String AMQP_PARTITION_ANNOTATION = "x-opt-bridge.partition";
	public static final String AMQP_KEY_ANNOTATION = "x-opt-bridge.key";
}
