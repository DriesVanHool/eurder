package com.switchfully.eurder.api.dtos;

import com.switchfully.eurder.domain.StockLvl;

public record ItemDto(String id, String name, String description, double price, int amount, StockLvl stockLvl) {
}
