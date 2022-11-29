package com.switchfully.eurder.api.dtos;

import java.util.List;

public record OrderDto(double totalPrice, List<ItemGroupDto> itemGroups) {
}
