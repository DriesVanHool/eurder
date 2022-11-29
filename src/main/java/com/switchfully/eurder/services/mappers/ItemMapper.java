package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.ItemDto;
import com.switchfully.eurder.domain.Item;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getPrice(), item.getAmount(), item.getStockLvl());
    }

    public List<ItemDto> toDto(List<Item> items) {
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }
}
