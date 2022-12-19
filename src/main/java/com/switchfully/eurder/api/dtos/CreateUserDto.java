package com.switchfully.eurder.api.dtos;

import com.switchfully.eurder.domain.Adress;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateUserDto(@NotBlank(message = "Firstname needs to be filled in") String firstname,
                            @NotBlank(message = "Lastname needs to be filled in") String lastname,
                            @Email(message = "Email is not valid")
                            @NotBlank(message = "Email needs to be filled in")
                            String email,
                            @NotBlank(message = "Phone number needs to be filled in")
                            String phoneNumber,
                            @NotBlank(message = "Street needs to be filled in")
                            String street,
                            @NotBlank(message = "House number needs to be filled in")
                            String houseNumber,
                            @NotBlank(message = "Zip needs to be filled in")
                            String zip,
                            @NotBlank(message = "Password needs to be filled in")
                            String password) {
}
