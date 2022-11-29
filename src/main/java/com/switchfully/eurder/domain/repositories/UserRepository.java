package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.security.Role;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final Map<String, User> userMap = new HashMap<>();

    public UserRepository() {
        userMap.put("1", new User("1", "Dries", "Van Hool", "driesvanhool@gmail.com", null, "pwd", Role.ADMIN));
    }

    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(userMap.get(id));
    }

    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }
}
