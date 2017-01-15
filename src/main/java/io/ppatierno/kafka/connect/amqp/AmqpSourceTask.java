package io.ppatierno.kafka.connect.amqp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonServer;

/**
 * AmqpSourceTask is a Kafka Connect task that receives messages
 * on AMQP protocol and generates Kafka Connect records
 */
public class AmqpSourceTask extends SourceTask {
	
	private static final Logger log = LoggerFactory.getLogger(AmqpSourceTask.class);

	Queue<AmqpSourceMessage> queue = new LinkedList<>();
	
	int serverPort;
	int receiverCredits;
	
	public String version() {
		log.info("AmqpSourceTask.version");
		return "0";
	}

	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		
		List<SourceRecord> records = new ArrayList<>();
		
		while (this.queue.peek() != null) {
			
			AmqpSourceMessage msg = this.queue.poll();
			
			Section body = msg.getMessage().getBody();
            if (body instanceof AmqpValue) {
            	
            	String kafkaTopic = msg.getKafkaTopic();
            	
                String content = (String) ((AmqpValue) body).getValue();
                
                Integer kafkaPartition = null;
                if (msg.getMessage().getApplicationProperties() != null) {
                	kafkaPartition = (Integer)msg.getMessage().getApplicationProperties().getValue().get(AmqpSourceConnectorConstant.PARTITION_PROP);
                }
                
                log.info("poll : message on " + kafkaTopic + " partition " + kafkaPartition + " content " + content);
                
                SourceRecord record = new SourceRecord(null, null, 
                										kafkaTopic, kafkaPartition, 
                										Schema.STRING_SCHEMA, kafkaTopic, 
                										Schema.STRING_SCHEMA, content);
                
                records.add(record);
                
                log.info("poll : message on " + kafkaTopic + " content " + content);                
            }
		}
		
		return records;
	}

	@Override
	public void start(Map<String, String> props) {
		log.info("AmqpSourceTask.start");
		
		this.serverPort = Integer.valueOf(props.get(AmqpSourceConnectorConstant.SERVER_PORT));
		this.receiverCredits = Integer.valueOf(props.get(AmqpSourceConnectorConstant.SERVER_CREDITS));
		
		Vertx vertx = Vertx.vertx();
		
		ProtonServer server = ProtonServer.create(vertx)
				.connectHandler((connection) -> {
					processConnection(connection);
				})
				.listen(this.serverPort, (res) -> {
					
					if (res.succeeded()) {
						log.info("Listening on: " + res.result().actualPort());
					} else {
						res.cause().printStackTrace();
					}
				});
	}

	@Override
	public void stop() {
		log.info("AmqpSourceTask.stop");
	}
	
	@Override
	public void initialize(SourceTaskContext context) {
		super.initialize(context);
		log.info("AmqpSourceTask.initialize");
	}
	
	private void processConnection(ProtonConnection connection) {
		log.info("AmqpSourceTask.processConnection");
		
		connection.openHandler(res ->{
			log.info("Client connected: " + res.result().getRemoteContainer());
			
        }).closeHandler(res -> {
        	log.info("Client closing connection: " + res.result().getRemoteContainer());
        	res.result().close();
        	res.result().disconnect();
        	
        }).disconnectHandler(conn -> {
            log.info("Client disconnected: " + conn.getRemoteContainer());
            conn.disconnect();
            
        }).open();
		
        connection.sessionOpenHandler(session -> session.open());
        
		connection.receiverOpenHandler(receiver -> {
			receiver
            .setTarget(receiver.getRemoteTarget())
            .handler((delivery, msg) -> {

                String address = msg.getAddress();
                if( address == null ) {
                    address = receiver.getRemoteTarget().getAddress();
                }
                
                AmqpSourceMessage message = new AmqpSourceMessage(address, delivery, msg);

                this.queue.add(message);
            })
            .setPrefetch(this.receiverCredits)
            .open();
			
		});
	}

}
