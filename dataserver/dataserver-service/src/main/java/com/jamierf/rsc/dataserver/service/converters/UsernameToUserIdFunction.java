package com.jamierf.rsc.dataserver.service.converters;

import com.google.common.base.Function;

import javax.annotation.Nullable;

public class UsernameToUserIdFunction implements Function<String, Long> {

    public static final UsernameToUserIdFunction INSTANCE = new UsernameToUserIdFunction();

    private UsernameToUserIdFunction() {}

    @Override
    public Long apply(@Nullable String username) {
        if (username == null) {
            return 0L;
        }

        return 1L; // TODO!!!
    }
}
