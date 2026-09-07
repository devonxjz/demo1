package dev.repositories;

import dev.models.User;

public class UserRepository extends BaseRepository<User, Long> {

    public UserRepository() {
        super(User.class);
    }
}
