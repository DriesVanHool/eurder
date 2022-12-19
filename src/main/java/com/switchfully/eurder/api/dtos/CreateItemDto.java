package com.switchfully.eurder.api.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

public record CreateItemDto(@NotBlank(message = "Name needs to be filled in") String name,
                            @NotBlank(message = "Description needs to be filled in") String description,
                            @PositiveOrZero (message = "Price needs to be filled in") double price,
                            @PositiveOrZero (message = "Amount needs to be filled in") int amount) {
}
