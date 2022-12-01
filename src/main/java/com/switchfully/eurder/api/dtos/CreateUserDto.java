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
                            @NotBlank(message = "Phonenumber needs to be filled in")
                            String phoneNumber,
                            @NotNull(message = "Adress needs to be filled in")
                            Adress adress,
                            @NotBlank(message = "Password needs to be filled in")
                            String password) {
}
