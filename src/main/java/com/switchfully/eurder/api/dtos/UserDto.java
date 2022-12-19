package com.switchfully.eurder.api.dtos;

import com.switchfully.eurder.domain.Adress;

public record UserDto(int id, String firstname, String lastname, String email, String phoneNumber, Adress adress) {

}
