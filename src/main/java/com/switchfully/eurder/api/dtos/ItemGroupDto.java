package com.switchfully.eurder.api.dtos;

import java.time.LocalDate;

public record ItemGroupDto(int itemId, int amount, LocalDate shippingDate, double price) {
}
