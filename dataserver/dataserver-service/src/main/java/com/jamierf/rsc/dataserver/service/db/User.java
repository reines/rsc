package com.jamierf.rsc.dataserver.service.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.jamierf.rsc.dataserver.api.UserData;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class User extends UserData implements Serializable {

    protected static final String USERNAME_FIELD = "username";

    private static String capitalize(String input) {
        final Iterable<String> lowercaseParts = Splitter.on(' ').split(input.toLowerCase());

        final Iterable<String> capitalizedParts = Iterables.transform(lowercaseParts, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return StringUtils.capitalize(input);
            }
        });

        return Joiner.on(' ').join(capitalizedParts);
    }

    public static String cleanUsername(String username) {
        // Replace multiple whitespace with a single
        username = username.replaceAll("\\s", " ");

        // Trim any whitespace from the ends
        username = username.trim();

        // Lowercase then capitalize the first letter of every word
        username = User.capitalize(username);

        return username;
    }

    private Password password;

    protected User() { }

    protected User(String username, String password) {
        super.username = username;
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
