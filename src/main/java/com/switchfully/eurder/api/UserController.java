package com.switchfully.eurder.api;

import com.switchfully.eurder.api.dtos.CreateUserDto;
import com.switchfully.eurder.api.dtos.UserDto;
import com.switchfully.eurder.services.KeyCloakService;
import com.switchfully.eurder.services.TestService;
import com.switchfully.eurder.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "users")
public class UserController {
    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private final UserService userService;
    private final KeyCloakService keyCloakService;

    public UserController(UserService userService, KeyCloakService cloakService) {
        this.userService = userService;
        this.keyCloakService = cloakService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createCustomer(@Valid @RequestBody CreateUserDto createUserDto) {
        UserDto userDto = userService.createCustomer(createUserDto);
        if (!createUserDto.email().contains("test")){
            keyCloakService.addUser(createUserDto);
        }
        return userDto;
    }

   @GetMapping(path = "customers")
   @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> getAllCustomers() {
        return userService.getAllCustomers();
    }

    @GetMapping(path = "customers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto getCustomerById(@PathVariable String id) {
        return userService.getCustomerById(id);
    }
}
