package com.jamierf.rsc.dataserver.service.db;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.jamierf.rsc.dataserver.api.UserData;
import com.jamierf.rsc.dataserver.service.converters.UserAndPasswordToUserDataFunction;
import com.jamierf.rsc.dataserver.service.converters.UsernameToUserIdFunction;
import com.jamierf.rsc.dataserver.service.converters.UsernameCleaningFunction;
import com.jamierf.rsc.dataserver.service.error.UserAlreadyExistsException;
import com.jamierf.rsc.dataserver.service.model.Password;
import com.jamierf.rsc.dataserver.service.model.UserAndPassword;

import java.util.Map;

public class UserStore {

    private final Map<String, UserAndPassword> users;

    public UserStore() {
        users = Maps.newHashMap(); // TODO: Not in memory
    }

    public synchronized UserData create(String username, String password) throws UserAlreadyExistsException {
        // TODO: Check the user doesn't already exist

        final String cleanedUsername = UsernameCleaningFunction.INSTANCE.apply(username);
        if (users.containsKey(cleanedUsername)) {
            throw new UserAlreadyExistsException(username);
        }

        final long userId = UsernameToUserIdFunction.INSTANCE.apply(cleanedUsername);

        final UserAndPassword profile = new UserAndPassword(UserData.create(userId, cleanedUsername), Password.fromString(password));
        users.put(cleanedUsername, profile);

        return UserAndPasswordToUserDataFunction.INSTANCE.apply(profile);
    }

    private Optional<UserAndPassword> findUserAndPasswordByUsername(String username) {
        final String cleanedUsername = UsernameCleaningFunction.INSTANCE.apply(username);
        return Optional.fromNullable(users.get(cleanedUsername));
    }

    public Optional<UserData> findByUsername(String username) {
        return findUserAndPasswordByUsername(username)
                .transform(UserAndPasswordToUserDataFunction.INSTANCE);
    }

    public Optional<UserData> findByCredentials(String username, String password) {
        final Optional<UserAndPassword> profile = findUserAndPasswordByUsername(username);
        if (!profile.isPresent()) {
            return Optional.absent();
        }

        // Check the password matches
        if (!profile.get().getPassword().isMatch(password)) {
            return Optional.absent();
        }

        return profile.transform(UserAndPasswordToUserDataFunction.INSTANCE);
    }
}
