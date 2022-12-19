package com.switchfully.eurder.api.dtos;

import javax.validation.constraints.PositiveOrZero;

public record CreateItemGroupDto(@PositiveOrZero(message = "Item ID needs to be filled in") int itemId,
                                 @PositiveOrZero (message = "Amount needs to be filled in")int amount) {
}
