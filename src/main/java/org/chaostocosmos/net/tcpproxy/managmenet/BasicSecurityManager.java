package org.chaostocosmos.net.tcpproxy.managmenet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.security.auth.Subject;

import org.chaostocosmos.net.tcpproxy.Logger;
import org.chaostocosmos.net.tcpproxy.credential.Credential;
import org.chaostocosmos.net.tcpproxy.credential.Credentials;
import org.chaostocosmos.net.tcpproxy.credential.Grant;
import org.chaostocosmos.net.tcpproxy.credential.Session;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;

/**
 * BasicSecurityManager
 * 
 * @author Kooin-Shin
 */
public class BasicSecurityManager {
	
	private Map<String, SecurityHandler> sessionSecurityHandlerMap;

	private Credentials credentials;
	
	/**
	 * Constructor
	 * @param credentials
	 */
	public BasicSecurityManager(Credentials credentials) {
		this.credentials = credentials;
		this.sessionSecurityHandlerMap = new HashMap<>();
		for(Session session : this.credentials.getSessions()) {
			this.sessionSecurityHandlerMap.put(session.getSession(), createBasicSecurityHandler(session, session.getSession(), "/*"));
		}
	}
	
	public List<String> getRealmsByUser(final String username) {
		return this.credentials.getSessions().stream().filter(s -> s.getCredential().stream().anyMatch(c -> c.getUsername().equals(username))).map(a -> a.getSession()).collect(Collectors.toList());
	}
	
	public UserStore createUserStore(List<Credential> credentialConfigList) {
		UserStore userStore = new UserStore();
		credentialConfigList.stream().forEach(c -> userStore.addUser(c.getUsername(), new Password(c.getPassword()), c.getGrant().toArray(new String[0])));
		return userStore;
	}

	public SecurityHandler getSessionSecurityHandler(String sessionName) {
		return this.sessionSecurityHandlerMap.get(sessionName);
	}
	
	private SecurityHandler createBasicSecurityHandler(Session session, String realm, String pathSpec) {
    	HashLoginService loginService = new HashLoginService(realm);
    	loginService.setName(realm);
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName(realm);
    	
		UserStore userStore = new UserStore();
    	for(Credential credential : session.getCredential()) {
    		userStore.addUser(credential.getUsername(), new Password(credential.getPassword()), credential.getGrant().toArray(new String[0]));
    		Logger.getInstance().debug("$$ add user - "+credential.getUsername()+"   "+credential.getPassword()+"   "+credential.getGrant().toString());
            Constraint constraint = new Constraint();
            constraint.setName(Constraint.__BASIC_AUTH);
            constraint.setRoles(credential.getGrant().toArray(new String[0]));
			constraint.setAuthenticate(true);
            ConstraintMapping cm = new ConstraintMapping();
            cm.setConstraint(constraint);
            cm.setPathSpec(pathSpec);
            csh.addConstraintMapping(cm);
		}
    	loginService.setUserStore(userStore);
		csh.setLoginService(loginService);
		csh.setSessionRenewedOnAuthentication(true);
        return csh;
	}

	private SecurityHandler getSecurityHandler(String realm, String pathSpec) {
		HashLoginService loginService = new HashLoginService();
		loginService.setName(realm);
		UserStore userStore = new UserStore();
		userStore.addUser("admin", new Password("admin"), new String[] {"user"});
		loginService.setUserStore(userStore);

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[]{Constraint.ANY_AUTH, Grant.ADMIN.name()
															, Grant.OPERATOR.name()
															, Grant.MONITOR.name()
															, Grant.GUEST.name()
															});
		constraint.setAuthenticate(true);
		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec(pathSpec);
		securityHandler.addConstraintMapping(cm);		
		securityHandler.setAuthenticator(new BasicAuthenticator());
		securityHandler.setRealmName(realm);
		securityHandler.setLoginService(loginService);
		((HashLoginService)securityHandler.getLoginService()).setUserStore(userStore);
		return securityHandler;
	}
	
    private final SecurityHandler basicAuth(String username, String password, String realm, String pathSpec) {
    	HashLoginService lservice = new HashLoginService();
    	UserStore userStore = new UserStore();
    	userStore.addUser(username, org.eclipse.jetty.util.security.Credential.getCredential(password), new String[] {"user"});
    	lservice.setUserStore(userStore);
        lservice.setName(realm);
        
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);
         
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec(pathSpec);
        System.out.println("path spec: "+cm.getPathSpec());
        
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName(realm);
        csh.addConstraintMapping(cm);
        csh.setLoginService(lservice);        
        return csh;
    }


}