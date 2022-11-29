package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.ItemGroupDto;
import com.switchfully.eurder.domain.ItemGroup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemGroupMapper {
    public ItemGroupDto toDto(ItemGroup itemGroup) {
        return new ItemGroupDto(itemGroup.getItemId(), itemGroup.getAmount(), itemGroup.getShippingDate(), itemGroup.getBuyPrice());
    }

    public List<ItemGroupDto> toDTO(List<ItemGroup> itemGroups) {
        return itemGroups.stream().map(this::toDto).toList();
    }
}
