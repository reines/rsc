package com.jamierf.rsc.dataserver.service.db;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Password implements Serializable {

    private String salt;
    private String hash;

    protected Password() { }

    public Password(String input) {
        salt = BCrypt.gensalt();
        hash = BCrypt.hashpw(input, salt);
    }

    public boolean isMatch(String input) {
        return BCrypt.checkpw(input, hash);
    }

    @Basic
    protected String getSalt() {
        return salt;
    }

    protected void setSalt(String salt) {
        this.salt = salt;
    }

    @Basic
    protected String getHash() {
        return hash;
    }

    protected void setHash(String hash) {
        this.hash = hash;
    }
}
