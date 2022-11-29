package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.domain.security.Feature;
import com.switchfully.eurder.services.SecurityService;
import com.switchfully.eurder.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping(path = "users")
public class UserController {
    private final UserService userService;
    private final SecurityService securityService;

    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createCustomer(@RequestBody CreateUserDto createUserDto) {
        return userService.createCustomer(createUserDto);
    }

    @GetMapping(path = "customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getAllCustomers(@RequestHeader String authorization) {
        securityService.validateAuthorisation(authorization, Feature.GET_CUSTOMERS);
        return userService.getAllCustomers();
    }

    @GetMapping(path = "customers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getCustomerById(@PathVariable String id, @RequestHeader String authorization) {
        securityService.validateAuthorisation(authorization, Feature.GET_CUSTOMERS);
        return userService.getCustomerById(id);
    }
}
