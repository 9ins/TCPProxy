package org.chaostocosmos.net.tcpproxy;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.chaostocosmos.net.tcpproxy.config.ConfigHandler;
import org.chaostocosmos.net.tcpproxy.credential.CredentialsHandler;
import org.chaostocosmos.net.tcpproxy.managmenet.ManagementServer;
import org.junit.Before;
import org.junit.Test;

public class testManagementServer {

	Path configPath;
    ConfigHandler configHandler;
	CredentialsHandler credentialsHandler;
	
	@Before
	public void before_test() throws Exception {
		this.configPath = Paths.get("D:/Projects/TCPProxy/config.yml");
	}

	@Test
    public void test_ManagementServer() throws Exception {
		this.configHandler = ConfigHandler.getInstance(this.configPath);
		Path credentialPath = Paths.get(this.configHandler.getConfig().getCredentialPath());
		this.credentialsHandler = CredentialsHandler.getInstance(credentialPath);
		ManagementServer server = new ManagementServer(null, this.configHandler, this.credentialsHandler);
	}
	
	public static void main(String[] args) throws Exception {
		testManagementServer server = new testManagementServer();
		server.before_test();
		server.test_ManagementServer();
	}
}
