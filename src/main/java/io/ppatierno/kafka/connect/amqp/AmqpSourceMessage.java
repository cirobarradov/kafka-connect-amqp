package io.ppatierno.kafka.connect.amqp;

import org.apache.qpid.proton.message.Message;

import io.vertx.proton.ProtonDelivery;

/**
 * Class for brinding AMQP message information
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
