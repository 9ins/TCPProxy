package org.chaostocosmos.net.tcpproxy.config;

/**
 * 
 * ConfigException
 *
 * @author 9ins
 * 2020. 11. 26.
 */
public class ConfigException extends RuntimeException {
	
	String configKey;

	/**
	 * Constructor
	 * 
	 * @param configKey
	 * @param msg
	 */
	public ConfigException(String configKey, String msg) {
		super(msg);
		this.configKey = configKey;
	}
	
	/**
	 * Get configuration key
	 * @return
	 */
	public String getConfigKey() {
		return this.configKey;
	}

}
