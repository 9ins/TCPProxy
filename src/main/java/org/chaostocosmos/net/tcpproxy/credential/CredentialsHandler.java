package org.chaostocosmos.net.tcpproxy.credential;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.chaostocosmos.net.tcpproxy.TCPProxy;
import org.chaostocosmos.net.tcpproxy.config.ConfigException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

public class CredentialsHandler {
	private static CredentialsHandler configHandler;
	Path credentialsPath;
	Credentials credentials;
	
	/**
	 * Get instance of CredentialsHandler
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static CredentialsHandler getInstance() throws FileNotFoundException {
		return getInstance(TCPProxy.credentialPath);
	}
	
	/**
	 * Get instance of CredentialsHandler
	 * @param credentialsPath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static CredentialsHandler getInstance(Path credentialsPath) throws FileNotFoundException {
		if(configHandler == null) {
			configHandler = new CredentialsHandler(credentialsPath);
		}
		return configHandler;
	}
	
	/**
	 * Constructor
	 * @param credentialsPath
	 * @throws FileNotFoundException 
	 */
	private CredentialsHandler(Path credentialsPath) throws FileNotFoundException {
		this.credentialsPath = credentialsPath;		
		loadConfig();		
		
		/*
		this.config = new Config();
		Map<String, SessionMapping> map = new HashMap<>();
		
		SessionMapping sm = new SessionMapping();
		map.put("MySQL", sm);
		SessionMapping sm1 = new SessionMapping();
		map.put("Oracle", sm1);
		SessionMapping sm2 = new SessionMapping();
		map.put("Kafka", sm2);
		
		this.config.setSessionMapping(map);
		*/
	}
	
	/**
	 * Load config.yml 
	 * @throws FileNotFoundException
	 */
	public void loadConfig() throws FileNotFoundException {
		loadConfig(this.credentialsPath);
	}
	 
	/**
	 * Load config.yml
	 * @param configPath
	 * @throws FileNotFoundException
	 */
	public void loadConfig(Path credentialsPath) throws FileNotFoundException {
		Constructor constructor = new Constructor(Credentials.class);
		Yaml yaml = new Yaml(constructor);
		this.credentials = yaml.load(new FileInputStream(credentialsPath.toFile()));
		verifyConfig(this.credentials);
	}
	
	/**
	 * Dump Credentials to credentials.yml
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void dumpCredentials() throws IllegalArgumentException, IllegalAccessException, IOException {
		dumpCredentials(this.credentialsPath);
	}
	
	/**
	 * Dump Credentials to credentials.yml
	 * @param configPath
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void dumpCredentials(Path credentialsPath) throws IllegalArgumentException, IllegalAccessException, IOException {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);		
		Yaml yml = new Yaml(options);
		yml.dump(this.credentials, new FileWriter(credentialsPath.toFile()));
	}
	
	/**
	 * Dump Credentials with specified
	 * @param credentials
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public void dumpCredentials(Credentials credentials) throws IllegalArgumentException, IllegalAccessException, IOException {
		this.credentials = credentials;
		dumpCredentials();
	}
	
	/**
	 * Verify yaml credentials file
	 * @param credentials
	 */
	public void verifyConfig(Credentials credentials) {
        for(Session session : credentials.getSessions()) {
            List<Credential> list = session.getCredential();

            for(Credential credential : list) {
                if(!credential.getGrant().stream().anyMatch(g -> g.equals(Grant.ADMIN.name())
                                                             || g.equals(Grant.OPERATOR.name())
                                                             || g.equals(Grant.MONITOR.name()) 
                                                             || g.equals(Grant.GUEST.name())
                                                             )) {
                    throw new ConfigException("credential", "[Credentials:Sessions:"+session.getSession()+"] Grant for session must be defined among ADMIN/OPERATOR/MONITOR/GUEST. Wrong grant: "+credential.getGrant());
                }
            }
        }
	}   
	
	/**
	 * Get super user
	 * @return
	 */
	public String getSuperUser() {
		return this.credentials.getSuperUser();
	}
	
	/**
	 * Set super user
	 * @param superUser
	 */
	public void setSuperUser(String superUser) {
		this.credentials.setSuperUser(superUser);
	}
	
	/**
	 * Get super password
	 * @return
	 */
	public String getSuperPassword() {
		return this.credentials.getSuperPassword();
	}
	
	/**
	 * Set super password
	 * @param superPassword
	 */
	public void setAdminPassword(String superPassword) {
		this.credentials.setSuperPassword(superPassword);
	}
	
	/**
	 * Get Credentials object
	 * @return
	 */
	public Credentials getCredentials() {
		return this.credentials;
	}
	
	/**
	 * Set Credentials object
	 * @param Credentials
	 */
	public void setConfig(Credentials credentials) {
		this.credentials = credentials;
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
		Path path = Paths.get("./Credentials.yml");
		CredentialsHandler handler = new CredentialsHandler(path);
		handler.loadConfig();
		System.out.println("ADMIN: "+handler.getSuperUser());
		System.out.println("PASSWD: "+handler.getSuperPassword());
		
		//handler.setSessionMappingValue("Oracle", "readTimeout", new Integer(150));
		//handler.dumpCredentials();
	}    
    
}
