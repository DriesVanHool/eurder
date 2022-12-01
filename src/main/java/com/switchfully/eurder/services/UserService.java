package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.exceptions.InvallidInputException;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import com.switchfully.eurder.services.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto createCustomer(CreateUserDto createUserDto) throws InvallidInputException {
        User user = new User(createUserDto.firstname(), createUserDto.lastname(), createUserDto.email(), createUserDto.phoneNumber(), createUserDto.adress(), createUserDto.password(), Role.CUSTOMER);
        assertDoubleUsers(user);
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserDto> getAllCustomers() {
        return userMapper.toDto(userRepository.getAllUsers().stream().filter(user -> user.getRole() == Role.CUSTOMER).toList());
    }

    public UserDto getCustomerById(String id) throws IllegalArgumentException {
        return userMapper.toDto(userRepository.getUserById(id).orElseThrow(() -> new IllegalArgumentException("User id " + id + " not found.")));
    }

    public void assertDoubleUsers(User user) {
        if (userRepository.getAllUsers().contains(user)) {
            throw new IllegalArgumentException("This user already exists");
        }
        for (User value : userRepository.getAllUsers()) {
            if (value.getEmail().equals(user.getEmail()))
                throw new IllegalArgumentException("This emailadress already has an account");
        }
    }
}
