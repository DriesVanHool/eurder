package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> getUsersByRole(Role role);
    List<User> getUsersById(int id);
    List<User> getUserByEmail(String email);
}
