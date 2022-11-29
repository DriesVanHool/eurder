package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.Role;
import com.switchfully.eurder.services.mappers.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto createCustomer(CreateUserDto createUserDto) throws IllegalArgumentException {
        User user = new User(createUserDto.firstname(), createUserDto.lastname(), createUserDto.email(), createUserDto.phoneNumber(), createUserDto.adress(), createUserDto.password(), Role.CUSTOMER);
        String error = validateUserInput(createUserDto);
        if (!error.isEmpty()) throw new IllegalArgumentException("The following fields are invalid:" + error);
        return userMapper.toDto(userRepository.save(user));
    }

    public String validateUserInput(CreateUserDto createUserDto) {
        String result = "";
        if (createUserDto.firstname().isEmpty()) {
            result += " firstname ";
        }
        if (createUserDto.lastname().isEmpty()) {
            result += " lastname ";
        }
        if (!Helper.checkMail(createUserDto.email())) {
            result += " email ";
        }
        if (createUserDto.adress().street().isEmpty()) {
            result += " street ";
        }
        if (createUserDto.adress().houseNumber().isEmpty()) {
            result += " house number ";
        }
        if (createUserDto.adress().houseNumber().isEmpty()) {
            result += " city ";
        }
        if (createUserDto.password().isEmpty()) {
            result += " password ";
        }
        return result;
    }

}
