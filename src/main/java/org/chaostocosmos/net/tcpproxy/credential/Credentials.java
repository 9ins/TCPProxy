package org.chaostocosmos.net.tcpproxy.credential;

import java.util.List;

/**
 * TCPProxy Credentials for users
 */
public class Credentials {

    String superUser;
    String superPassword;
    List<Session> sessions;

    /**
     * Default constructor
     */
    public Credentials() {}

    public String getSuperUser() {
        return this.superUser;
    }

    public void setSuperUser(String superUser) {
        this.superUser = superUser;
    }

    public String getSuperPassword() {
        return this.superPassword;
    }

    public void setSuperPassword(String superPassword) {
        this.superPassword = superPassword;
    }

    public List<Session> getSessions() {
        return this.sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        return "{" +        
            " superUser='" + superUser + "'" +
            ", superPassword='" + superPassword + "'" +
            ", sessions='" + sessions + "'" +
            "}";
    }    
}
