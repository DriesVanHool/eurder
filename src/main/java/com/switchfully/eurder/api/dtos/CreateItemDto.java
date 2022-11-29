package com.switchfully.eurder.api.dtos;

public record CreateItemDto(String name, String description, double price, int amount) {
}
