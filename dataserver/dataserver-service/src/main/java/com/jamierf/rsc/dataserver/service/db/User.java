package com.jamierf.rsc.dataserver.service.db;

import com.google.common.hash.Hashing;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

@Entity
public class User implements Serializable {

    private static byte[] hash(byte[] password) {
        return Hashing.sha512().hashBytes(password).asBytes();
    }

    @Id
    @GeneratedValue
    private long id;

    @Column( unique = true, nullable = false )
    @Index( name = "username_idx" )
    private String username;

    @Basic
    private byte[] password;

    @Basic
    private short status;

    protected User() {}

    protected User(String username, byte[] password) {
        this.username = username;
        this.password = User.hash(password);
        this.status = 0;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isBanned() {
        return (status & 0x01) > 0;
    }

    public boolean isSuspended() {
        return (status & 0x02) > 0;
    }

    public boolean isMember() {
        return (status & 0x03) > 0;
    }

    public boolean isSuspectedStolen() {
        return (status & 0x04) > 0;
    }

    public boolean isVeteran() {
        return (status & 0x05) > 0;
    }

    public boolean isPasswordMatch(byte[] attempt) {
        return Arrays.equals(User.hash(attempt), password);
    }
}
