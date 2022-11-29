package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getPhoneNumber(), user.getAdress());
    }

    public List<UserDto> toDto(List<User> users) {
        return users.stream().map(this::toDto).collect(Collectors.toList());
    }
}
