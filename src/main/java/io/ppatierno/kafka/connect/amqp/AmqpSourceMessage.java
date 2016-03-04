package io.ppatierno.kafka.connect.amqp;

import org.apache.qpid.proton.message.Message;

import io.vertx.proton.ProtonDelivery;

public class AmqpSourceMessage {

	private String kafkaTopic;
	private ProtonDelivery delivery;
	private Message message;
	
	public AmqpSourceMessage(String kafkaTopic, ProtonDelivery delivery, Message message) {
		this.kafkaTopic = kafkaTopic;
		this.delivery = delivery;
		this.message = message;
	}
	
	public String getKafkaTopic() {
		return this.kafkaTopic;
	}
	
	public ProtonDelivery getDelivery() {
		return this.delivery;
	}
	
	public Message getMessage() {
		return this.message;
	}
}
