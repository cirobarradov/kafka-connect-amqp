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

import org.apache.qpid.proton.message.Message;

import io.vertx.proton.ProtonDelivery;

/**
 * Class for bringing AMQP message information
 */
public class AmqpSourceMessage {

	private String kafkaTopic;
	private ProtonDelivery delivery;
	private Message message;

	/**
	 * Constructor
	 *
	 * @param kafkaTopic	Kafka topic
	 * @param delivery	AMQP delivery
	 * @param message	AMQP raw message
     */
	public AmqpSourceMessage(String kafkaTopic, ProtonDelivery delivery, Message message) {
		this.kafkaTopic = kafkaTopic;
		this.delivery = delivery;
		this.message = message;
	}

	/**
	 * Kafka topic
	 * @return
     */
	public String kafkaTopic() {
		return this.kafkaTopic;
	}

	/**
	 * AMQP delivery
	 * @return
     */
	public ProtonDelivery delivery() {
		return this.delivery;
	}

	/**
	 * AMQP raw message
	 * @return
     */
	public Message message() {
		return this.message;
	}
}
