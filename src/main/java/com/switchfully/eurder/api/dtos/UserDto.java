package com.switchfully.eurder.api.dtos;

import com.switchfully.eurder.domain.Adress;

public record UserDto(String id, String firstname, String lastname, String email, String phoneNumber, Adress adress) {

}
