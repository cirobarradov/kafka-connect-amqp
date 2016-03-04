package io.ppatierno.kafka.connect.amqp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AmqpSourceConnector is a Kafka Connect connector that can receive messages
 * on AMQP protocol and deliver them into Kafka topic
 */
public class AmqpSourceConnector extends SourceConnector {
	
	private static final Logger log = LoggerFactory.getLogger(AmqpSourceConnector.class);

	private AmqpSourceConnectorConfig config;
	
	@Override
	public void start(Map<String, String> props) {
		log.info("AmqpSourceConnector.start");
		this.config = new AmqpSourceConnectorConfig(props); 
	}

	@Override
	public void stop() {
		log.info("AmqpSourceConnector.stop");
	}

	@Override
	public Class<? extends Task> taskClass() {
		log.info("AmqpSourceConnector.taskClass");
		return AmqpSourceTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int maxTasks) {
		
		log.info("AmqpSourceConnector.taskConfigs maxTasks = " + maxTasks);
		
		ArrayList<Map<String, String>> configs = new ArrayList<>();
		
		if (this.config.serverSize() > maxTasks) {
			log.error("Wrong AMQP Connector configuration");
		} else {
			for (int i = 0; i < this.config.serverSize(); i++) {
				
				Map<String, String> props = new HashMap<>();
				
				for (Map.Entry<String, String> config : this.config.getProperties(i).entrySet()) {
					props.put(config.getKey(), config.getValue());
				}
				
				configs.add(props);
			}
		}
		
		return configs;
	}

	@Override
	public String version() {
		
		return "0";
	}
	
	@Override
	public void initialize(ConnectorContext ctx) {
		super.initialize(ctx);
		log.info("AmqpSourceConnector.initialize");
	}

}
