package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping(path = "users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createCustomer(@RequestBody CreateUserDto createUserDto) {
        return userService.createCustomer(createUserDto);
    }
}
