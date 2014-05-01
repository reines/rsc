package com.jamierf.rsc.dataserver.service.converters;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class UsernameCleaningFunction implements Function<String, String> {

    public static UsernameCleaningFunction INSTANCE = new UsernameCleaningFunction();

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

    private UsernameCleaningFunction() {}

    @Override
    public String apply(@Nullable String username) {
        if (username == null) {
            return "";
        }

        // Replace multiple whitespace with a single
        username = username.replaceAll("\\s", " ");

        // Trim any whitespace from the ends
        username = username.trim();

        // Lowercase then capitalize the first letter of every word
        return capitalize(username);
    }
}
