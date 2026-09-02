package dev.services;

import dev.models.User;
import dev.repositories.UserRepository;


public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public boolean saveSurvey(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        return userRepository.save(user);
    }
}
