package com.switchfully.eurder.api.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

public record CreateItemGroupDto(@NotBlank(message = "Item ID needs to be filled in")String itemId,
                                 @NotBlank (message = "Amount needs to be filled in")String amount) {
}
