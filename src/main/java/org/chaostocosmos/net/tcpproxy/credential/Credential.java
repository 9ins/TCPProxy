package org.chaostocosmos.net.tcpproxy.credential;

import java.util.List;
import java.util.Objects;

/**
 * Credential for a Session
 */
public class Credential {

    String username;
    String password;
    List<String> grant;

    /**
     * Constructor
     */
    public Credential() {
    }

    /**
     * Constructor
     */
    public Credential(String username, String password, List<String> grant) {
        this.username = username;
        this.password = password;
        this.grant = grant;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getGrant() {
        return this.grant;
    }

    public void setGrant(List<String> grant) {
        this.grant = grant;
    }

    public Credential username(String username) {
        this.username = username;
        return this;
    }

    public Credential password(String password) {
        this.password = password;
        return this;
    }

    public Credential grant(List<String> grant) {
        this.grant = grant;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Credential)) {
            return false;
        }
        Credential credential = (Credential) o;
        return Objects.equals(username, credential.username) && Objects.equals(password, credential.password) && Objects.equals(grant, credential.grant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, grant);
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", grant='" + getGrant() + "'" +
            "}";
    }    
}
