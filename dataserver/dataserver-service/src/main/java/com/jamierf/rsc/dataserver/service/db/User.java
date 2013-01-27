package com.jamierf.rsc.dataserver.service.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.hash.Hashing;
import com.jamierf.rsc.dataserver.api.UserData;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

@Entity
public class User extends UserData implements Serializable {

    private static byte[] hash(byte[] password) {
        return Hashing.sha512().hashBytes(password).asBytes();
    }

    private byte[] passwordHash;

    protected User() { }

    protected User(String username, byte[] password) {
        super.setUsername(username);
        this.setPasswordHash(User.hash(password));
    }

    @Id
    @GeneratedValue
    public long getUserId() {
        return super.getUserId();
    }

    @Column( unique = true, nullable = false )
    @Index( name = "username_idx" )
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Column ( nullable = false )
    @JsonIgnore
    protected byte[] getPasswordHash() {
        return passwordHash;
    }

    protected void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Basic
    @Override
    public boolean isBanned() {
        return super.isBanned();
    }

    @Basic
    @Override
    public boolean isSuspended() {
        return super.isSuspended();
    }

    @Basic
    @Override
    public boolean isMember() {
        return super.isMember();
    }

    @Basic
    @Override
    public boolean isSuspectedStolen() {
        return super.isSuspectedStolen();
    }

    @Basic
    @Override
    public boolean isVeteran() {
        return super.isVeteran();
    }

    @Basic
    @Override
    public boolean isModerator() {
        return super.isModerator();
    }

    public boolean isPasswordMatch(byte[] attempt) {
        return Arrays.equals(User.hash(attempt), passwordHash);
    }
}
