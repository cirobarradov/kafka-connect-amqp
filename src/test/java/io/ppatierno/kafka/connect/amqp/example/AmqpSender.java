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

package io.ppatierno.kafka.connect.amqp.example;

import io.ppatierno.kafka.connect.amqp.source.AmqpSourceConnectorConstant;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonHelper;
import io.vertx.proton.ProtonSender;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.MessageAnnotations;
import org.apache.qpid.proton.message.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AmqpSender {

	public static void main(String[] args) {

		(new AmqpSender()).run();
	}

	public void run() {

		Vertx vertx = Vertx.vertx();

		ProtonClient client = ProtonClient.create(vertx);

		client.connect("localhost", 5672, done -> {

			if (done.succeeded()) {
				System.out.println("Connection successfull");

				ProtonConnection connection = done.result();
				this.sendMessage(vertx, connection);
			}
		});


		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendMessage(Vertx vertx, ProtonConnection connection) {
		
		connection.open();
		
		String topic = "test";
		
		ProtonSender sender = connection.createSender(null);
		
		sender.open();
		
		vertx.setPeriodic(2000, timer -> {
			
			if (connection.isDisconnected()) {
				vertx.cancelTimer(timer);
			} else {
		
				System.out.println("Sending message to server");
				
				Message message = ProtonHelper.message(topic, "Hello World from " + connection.getContainer());

				// sending on specified partition
				this.addAnnotation(message, Symbol.valueOf(AmqpSourceConnectorConstant.AMQP_PARTITION_ANNOTATION), 0);
				// sending with a key
				this.addAnnotation(message, Symbol.valueOf(AmqpSourceConnectorConstant.AMQP_KEY_ANNOTATION), "my_key");

				sender.send(ProtonHelper.tag("my_tag"), message, delivery -> {
					System.out.println("The message was received by the server");
				});
			}
			
		});
	}

	private void addAnnotation(Message message, Symbol symbol, Object value) {
		MessageAnnotations messageAnnotations = message.getMessageAnnotations();

		if (messageAnnotations == null) {
			Map<Symbol, Object> map = new HashMap<>();
			messageAnnotations = new MessageAnnotations(map);
			message.setMessageAnnotations(messageAnnotations);
		}

		messageAnnotations.getValue().put(symbol, value);
	}

}
