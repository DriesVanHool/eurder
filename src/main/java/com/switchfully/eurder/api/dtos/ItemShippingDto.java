package com.switchfully.eurder.api.dtos;

import com.switchfully.eurder.domain.Adress;
import com.switchfully.eurder.domain.ItemGroup;

import java.util.List;

public record ItemShippingDto(ItemGroup itemGroup, Adress adress) {
}
