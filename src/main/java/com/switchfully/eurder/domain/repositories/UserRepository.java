package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    public Optional<User> getUserById(String username) {
        return null;
    }
}
