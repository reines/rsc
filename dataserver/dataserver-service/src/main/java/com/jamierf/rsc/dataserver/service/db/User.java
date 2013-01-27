package com.jamierf.rsc.dataserver.service.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jamierf.rsc.dataserver.api.UserData;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class User extends UserData implements Serializable {

    protected static final String USERNAME_FIELD = "username";

    private Password password;

    protected User() { }

    protected User(String username, String password) {
        super.setUsername(username);

        this.password = new Password(password);
    }

    @Id
    @GeneratedValue
    public long getUserId() {
        return super.getUserId();
    }

    @Column( unique = true, nullable = false, name = USERNAME_FIELD )
    @Index( name = "username_idx" )
    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Basic
    @JsonIgnore
    protected Password getPassword() {
        return password;
    }

    protected void setPassword(Password password) {
        this.password = password;
    }

    protected boolean isPasswordMatch(String input) {
        return password.isMatch(input);
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
}
