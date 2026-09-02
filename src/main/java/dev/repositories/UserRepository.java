package dev.repositories;

import dev.models.User;

/**
 * UserRepository: Kế thừa BaseRepository để có sẵn các thao tác CRUD cơ bản
 */
public class UserRepository extends BaseRepository<User, Long> {

    public UserRepository() {
        super(User.class);
    }
}
