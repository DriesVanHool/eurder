package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getFirstname(), user.getLastname(), user.getEmail(), user.getPhoneNumber(), user.getAdress());
    }
}
