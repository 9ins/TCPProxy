package org.chaostocosmos.net.tcpproxy.credential;

import java.util.List;
import java.util.Objects;

/**
 * Session credentials for included users in the session
 */
public class Session {

    String session;
    List<Credential> credential;

    /**
     * Constructor
     */
    public Session() {
    }

    /**
     * Constructor
     */
    public Session(String session, List<Credential> credential) {
        this.session = session;
        this.credential = credential;
    }

    public String getSession() {
        return this.session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public List<Credential> getCredential() {
        return this.credential;
    }

    public void setCredential(List<Credential> credential) {
        this.credential = credential;
    }

    public Session session(String session) {
        this.session = session;
        return this;
    }

    public Session credential(List<Credential> credential) {
        this.credential = credential;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Session)) {
            return false;
        }
        Session session = (Session) o;
        return Objects.equals(session, session.session) && Objects.equals(credential, session.credential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, credential);
    }

    @Override
    public String toString() {
        return "{" +
            " session='" + getSession() + "'" +
            ", credential='" + getCredential() + "'" +
            "}";
    }
}