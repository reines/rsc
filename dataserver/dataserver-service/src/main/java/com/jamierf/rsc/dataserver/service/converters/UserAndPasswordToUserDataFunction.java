package com.jamierf.rsc.dataserver.service.converters;

import com.google.common.base.Function;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.model.UserAndPassword;

import javax.annotation.Nullable;

public class UserAndPasswordToUserDataFunction implements Function<UserAndPassword, UserData> {

    public static final UserAndPasswordToUserDataFunction INSTANCE = new UserAndPasswordToUserDataFunction();

    private UserAndPasswordToUserDataFunction() {}

    @Nullable
    @Override
    public UserData apply(@Nullable UserAndPassword userAndPassword) {
        return userAndPassword == null ? null : userAndPassword.getUser();
    }
}
