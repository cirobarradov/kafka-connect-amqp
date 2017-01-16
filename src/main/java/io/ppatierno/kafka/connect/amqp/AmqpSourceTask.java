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

import io.ppatierno.kafka.connect.amqp.util.Version;
import io.vertx.core.Vertx;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonServer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * AmqpSourceTask is a Kafka Connect task that receives messages
 * on AMQP protocol and generates Kafka Connect records
 */
public class AmqpSourceTask extends SourceTask {
	
	private static final Logger LOG = LoggerFactory.getLogger(AmqpSourceTask.class);

	private Queue<AmqpSourceMessage> queue = new LinkedList<>();

	private String serverHostname;
	int serverPort;
	int receiverCredits;

	@Override
	public String version() {
		return Version.getVersion();
	}

	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		
		List<SourceRecord> records = new ArrayList<>();
		
		while (this.queue.peek() != null) {
			
			AmqpSourceMessage msg = this.queue.poll();
			
			Section body = msg.message().getBody();
            if (body instanceof AmqpValue) {
            	
            	String kafkaTopic = msg.kafkaTopic();
            	
                String content = (String) ((AmqpValue) body).getValue();
                
                Integer kafkaPartition = null;
                if (msg.message().getApplicationProperties() != null) {
                	kafkaPartition = (Integer)msg.message().getApplicationProperties().getValue().get(AmqpSourceConnectorConstant.PARTITION_PROP);
                }
                
                LOG.info("poll : message on " + kafkaTopic + " partition " + kafkaPartition + " content " + content);
                
                SourceRecord record = new SourceRecord(null, null, 
                										kafkaTopic, kafkaPartition, 
                										Schema.STRING_SCHEMA, kafkaTopic, 
                										Schema.STRING_SCHEMA, content);
                
                records.add(record);
                
                LOG.info("poll : message on " + kafkaTopic + " content " + content);
            }
		}
		
		return records;
	}

	@Override
	public void start(Map<String, String> props) {

		LOG.info("Start AMQP source task");

		this.serverHostname = props.get(AmqpSourceConnectorConstant.AMQP_SERVER_HOSTNAME);
		this.serverPort = Integer.valueOf(props.get(AmqpSourceConnectorConstant.AMQP_SERVER_PORT));
		this.receiverCredits = Integer.valueOf(props.get(AmqpSourceConnectorConstant.AMQP_SERVER_CREDITS));
		
		Vertx vertx = Vertx.vertx();
		
		ProtonServer server = ProtonServer.create(vertx)
				.connectHandler((connection) -> {
					processConnection(connection);
				})
				.listen(this.serverPort, this.serverHostname, done -> {
					
					if (done.succeeded()) {
						LOG.info("Listening on {}", done.result().actualPort());
					} else {
						done.cause().printStackTrace();
					}
				});
	}

	@Override
	public void stop() {
		LOG.info("Stop AMQP source connector");
	}
	
	@Override
	public void initialize(SourceTaskContext context) {

		super.initialize(context);
		LOG.info("AMQP source task initialized");
	}
	
	private void processConnection(ProtonConnection connection) {

		connection.openHandler(done ->{

			LOG.info("Client connected: {}", done.result().getRemoteContainer());
			
        }).closeHandler(done -> {

        	LOG.info("Client closing connection: {}", done.result().getRemoteContainer());
			done.result().close();
			done.result().disconnect();
        	
        }).disconnectHandler(conn -> {

            LOG.info("Client disconnected: {}", conn.getRemoteContainer());
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
