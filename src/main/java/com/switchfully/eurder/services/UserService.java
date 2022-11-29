package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateItemDto;
import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.exceptions.InvallidInputException;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import com.switchfully.eurder.services.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        ArrayList<String> errors = validateUserInput(createUserDto);
        if (errors.size() > 0) throw new InvallidInputException(errors);
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

    public ArrayList<String> validateUserInput(CreateUserDto createUserDto) {
        ArrayList<String> errors = new ArrayList<>();
        if (createUserDto.firstname().isEmpty()) {
            errors.add("firstname");
        }
        if (createUserDto.lastname().isEmpty()) {
            errors.add("lastname");
        }
        if (!Helper.checkMail(createUserDto.email())) {
            errors.add("email");
        }
        if (createUserDto.adress().street().isEmpty()) {
            errors.add("street");
        }
        if (createUserDto.adress().houseNumber().isEmpty()) {
            errors.add("house number");
        }
        if (createUserDto.adress().houseNumber().isEmpty()) {
            errors.add("city");
        }
        if (createUserDto.password().isEmpty()) {
            errors.add("password");
        }
        return errors;
    }
}
