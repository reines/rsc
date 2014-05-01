package com.jamierf.rsc.dataserver.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.jamierf.rsc.dataserver.api.UserData;
import org.apache.commons.lang3.StringUtils;

public class UserAndPassword {

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
        if (username == null) {
            return null;
        }

        // Replace multiple whitespace with a single
        username = username.replaceAll("\\s", " ");

        // Trim any whitespace from the ends
        username = username.trim();

        // Lowercase then capitalize the first letter of every word
        username = UserAndPassword.capitalize(username);

        return username;
    }

    @JsonProperty
    private final UserData user;

    @JsonProperty
    private final Password password;

    @JsonCreator
    public UserAndPassword(
            @JsonProperty("user") UserData user,
            @JsonProperty("password") Password password) {
        this.user = user;
        this.password = password;
    }

    public UserData getUser() {
        return user;
    }

    public Password getPassword() {
        return password;
    }
}
