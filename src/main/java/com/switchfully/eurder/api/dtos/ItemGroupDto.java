package com.switchfully.eurder.api.dtos;

import java.time.LocalDate;

public record ItemGroupDto(String itemId, int amount, LocalDate shippingDate, double price) {
}
