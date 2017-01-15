package io.ppatierno.kafka.connect.amqp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for the AMQP source connector
 */
public class AmqpSourceConnectorConfig {

	private static final Logger LOG = LoggerFactory.getLogger(AmqpSourceConnectorConfig.class);
	
	// list of configuration parameters for each server
	List<Map<String, String>> configs;

	/**
	 * Constructor
	 *
	 * @param props	properties map for configuration
     */
	public AmqpSourceConnectorConfig(Map<String, String> props) {
		
		LOG.info("Getting AMQP Connector configuration");
		
		this.configs = new ArrayList<>();
		
		int i = 0;
		// number of requested server
		int serverSize = Integer.valueOf(props.get(AmqpSourceConnectorConstant.SERVER_SIZE));
		
		for (i = 0; i < serverSize; i++) {
			
			// prepare new configuration for current server
			Map<String, String> config = new HashMap<>();
			this.configs.add(i, config);
			
			this.extractServerProperty(props, i, AmqpSourceConnectorConstant.SERVER_PORT);
			this.extractServerProperty(props, i, AmqpSourceConnectorConstant.SERVER_CREDITS);
		}
		
		// print configuration for each server
		for (Map<String, String> config : this.configs) {
			LOG.info("Configuration for server {}", i++);
			for (Map.Entry<String, String> prop : config.entrySet()) {
				LOG.info("{} = {}", prop.getKey(), prop.getValue());
			}
		}
	}
	
	/**
	 * Number of configured server 
	 * 
	 * @return	number of configured server
	 */
	public int serverSize() {
		return this.configs.size();
	}
	
	/**
	 * Provides configuration properties for a specific server
	 * 
	 * @param serverIndex	server index 													
	 * @return				configuration properties for the specified server
	 */
	public Map<String, String> getProperties(int serverIndex) {
		return this.configs.get(serverIndex);
	}
	
	/**
	 * Extract a specific configuration property for a specific server
	 * 
	 * @param props			properties collection
	 * @param serverIndex	server index to which to extract a property
	 * @param key			property key to extract
	 */
	private void extractServerProperty(Map<String, String> props, int serverIndex, String key) {
		
		String value = props.get(AmqpSourceConnectorConstant.SERVER_PREFIX.replace("{}", String.valueOf(serverIndex)) + key);
		if (value != null)
			this.configs.get(serverIndex).put(key, value);
	}
}
