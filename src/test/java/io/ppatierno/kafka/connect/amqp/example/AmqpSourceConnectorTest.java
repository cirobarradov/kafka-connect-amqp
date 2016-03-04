package io.ppatierno.kafka.connect.amqp.example;

import java.io.IOException;

import org.apache.qpid.proton.message.Message;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonHelper;
import io.vertx.proton.ProtonSender;

public class AmqpSourceConnectorTest {

	public static void main(String[] args) {
		
		Vertx vertx = Vertx.vertx();
		
		ProtonClient client = ProtonClient.create(vertx);
		
		client.connect("localhost", 5672, res -> {
			if (res.succeeded()) {
				System.out.println("Connection successfull");
				
				ProtonConnection connection = res.result();
				sendMessage(vertx, connection);
			}
		});
		
		
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sendMessage(Vertx vertx, ProtonConnection connection) {
		
		connection.open();
		
		String topic = "my_topic";
		
		ProtonSender sender = connection.createSender(null);
		
		sender.open();
		
		vertx.setPeriodic(2000, timer -> {
			
			if (connection.isDisconnected()) {
				vertx.cancelTimer(timer);
			} else {
		
				System.out.println("Sending message to server");
				
				Message message = ProtonHelper.message(topic, "Hello World from " + connection.getContainer());
				
				sender.send(ProtonHelper.tag("m1"), message, delivery -> {
					System.out.println("The message was received by the server");
				});
			}
			
		});
	}

}
