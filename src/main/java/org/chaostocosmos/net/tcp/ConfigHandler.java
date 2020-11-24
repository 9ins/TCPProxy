package org.chaostocosmos.net.tcp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.representer.Representer;

/**
 * 
 * ConfigHandler - Managing configuration of TCP Proxy 
 *
 * @author 9ins
 * 2020. 11. 17.
 */
public class ConfigHandler {
	
	private static ConfigHandler configHandler;
	Path configPath;
	Config config;
	
	/**
	 * Get instance of ConfigHandler
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static ConfigHandler getInstance() throws FileNotFoundException {
		return getInstance(TCPProxy.configPath);
	}
	
	/**
	 * Get instance of ConfigHandler
	 * @param configPath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ConfigHandler getInstance(Path configPath) throws FileNotFoundException {
		if(configHandler == null) {
			configHandler = new ConfigHandler(configPath);
		}
		return configHandler;
	}
	
	/**
	 * Constructor
	 * @param configPath
	 * @throws FileNotFoundException 
	 */
	private ConfigHandler(Path configPath) throws FileNotFoundException {
		this.configPath = configPath;		
		loadConfig();
		
		/*
		this.config = new Config();
		Map<String, SessionMapping> map = new HashMap<>();
		SessionMapping sm = new SessionMapping();
		sm.setAllowedHosts(new ArrayList<String>());
		map.put("InnoQuartz", sm);
		SessionMapping sm1 = new SessionMapping();
		sm1.setAllowedHosts(new ArrayList<String>());
		map.put("Oracle", sm1);
		this.config.setSessionMapping(map);
		*/		
	}
	
	/**
	 * Load config.yml 
	 * @throws FileNotFoundException
	 */
	public void loadConfig() throws FileNotFoundException {
		loadConfig(this.configPath);
	}
	
	/**
	 * Load config.yml
	 * @param configPath
	 * @throws FileNotFoundException
	 */
	public void loadConfig(Path configPath) throws FileNotFoundException {
		Constructor constructor = new Constructor(Config.class);
		Yaml yaml = new Yaml(constructor);
		this.config = yaml.load(new FileInputStream(configPath.toFile()));
		verifyConfig(this.config);
	}
	
	/**
	 * Dump config to config.yml
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void dumpConfig() throws IllegalArgumentException, IllegalAccessException, IOException {
		dumpConfig(this.configPath);
	}
	
	/**
	 * Dump config to config.yml
	 * @param configPath
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void dumpConfig(Path configPath) throws IllegalArgumentException, IllegalAccessException, IOException {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);		
		Yaml yml = new Yaml(options);
		yml.dump(this.config, new FileWriter(configPath.toFile()));
	}
	
	/**
	 * Dump config with specified
	 * @param config
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void dumpConfig(Config config) throws IllegalArgumentException, IllegalAccessException, IOException {
		this.config = config;
		dumpConfig();
	}
	
	/**
	 * Verify yaml config file
	 * @param config
	 */
	public void verifyConfig(Config config) {
		if(config.getAdminPort() == 0) {
			throw new IllegalArgumentException("Can't use the specified port for admin port: "+config.getAdminPort());
		}
		if(config.getProxyHost() == null) {
			throw new IllegalArgumentException("Proxy host not specified in config.yml. Please check it.");
		}
		for(Entry<String, SessionMapping> entry : config.getSessionMapping().entrySet()) {			
			if(entry.getValue().getRemoteHost() == null) {
				throw new IllegalArgumentException("Remote target host not specified in config.yml. Please check session part: "+entry.getKey());
			}
			if(entry.getValue().getRemotePort() == 0) {
				throw new IllegalArgumentException("Can't use the specified port for remote port: "+entry.getValue().getRemotePort()+"  Check session part: "+entry.getKey());
			}
			if(entry.getValue().getProxyPort() == 0) {
				throw new IllegalArgumentException("TCPProxy server port must be defined a number except 0.  Check session part: "+entry.getKey());
			}
		}
	}
	
	/**
	 * Get administrator user
	 * @return
	 */
	public String getAdminUser() {
		return this.config.getAdminUser();
	}
	
	/**
	 * Set administrator user
	 * @param adminUser
	 */
	public void setAdminUser(String adminUser) {
		this.config.setAdminUser(adminUser);
	}
	
	/**
	 * Get administrator password
	 * @return
	 */
	public String getAdminPassword() {
		return this.config.getAdminPassword();
	}
	
	/**
	 * Set administrator password
	 * @param adminPassword
	 */
	public void setAdminPassword(String adminPassword) {
		this.config.setAdminPassword(adminPassword);
	}
	
	/**
	 * Get TCPProxy local binding address
	 * @return
	 */
	public String getProxyHost() {
		return this.config.getProxyHost();
	}
	
	/**
	 * Set TCPProxy local binding address
	 * @param adminAddress
	 */
	public void setProxyHost(String adminAddress) {
		this.config.setProxyHost(adminAddress);
	}
	
	/**
	 * Get TCPProxy local port
	 * @return
	 */
	public int getAdminPort() {
		return this.config.getAdminPort();
	}
	
	/**
	 * Set TCPProxy local port
	 * @param adminPort
	 */
	public void setAdminPort(int adminPort) {
		this.config.setAdminPort(adminPort);
	}
	
	/**
	 * Get config object
	 * @return
	 */
	public Config getConfig() {
		return this.config;
	}
	
	/**
	 * Set config object
	 * @param config
	 */
	public void setConfig(Config config) {
		this.config = config;
	}
	
	/**
	 * Get session mapping object by session name
	 * @param sessionName
	 * @return
	 */
	public SessionMapping getSessionMapping(String sessionName) {
		return this.config.getSessionMapping().get(sessionName);
	}
	
	/**
	 * Get session mapping object by source address and target port
	 * @param inetAddress
	 * @return
	 */
	public String getSessionName(Socket socket) {
		for(Entry<String, SessionMapping> entry : this.config.getSessionMapping().entrySet()) {
			String name = entry.getKey();
			SessionMapping sm = entry.getValue();
			if(sm.getAllowedHosts().stream().anyMatch(a -> a.equals(socket.getInetAddress().getHostAddress()))) {
				System.out.println("NAME: "+name);
				return name;
			}
		}
		return null;
	}
	
	/**
	 * Set session mapping object field value by name and value.
	 * @param sessionName
	 * @param fieldName
	 * @param value
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void setSessionMappingValue(String sessionName, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SessionMapping sm = getSessionMapping(sessionName);
		Field field = sm.getClass().getDeclaredField(fieldName);
		field.set(sm, value);
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
		Path path = Paths.get("./config.yml");
		ConfigHandler handler = new ConfigHandler(path);
		//handler.loadConfig();
		//System.out.println("ADMIN: "+handler.getAdminUser());
		//System.out.println("PASSWD: "+handler.getAdminPassword());
		//System.out.println("ORACLE: "+handler.getSessionMapping("Oracle").toString());
		
		//handler.setSessionMappingValue("Oracle", "readTimeout", new Integer(150));
		handler.dumpConfig();
	}
}
