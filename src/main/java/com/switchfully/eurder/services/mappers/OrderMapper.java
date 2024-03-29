package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.ItemGroupDto;
import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.domain.ItemGroup;
import com.switchfully.eurder.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public ItemGroupDto toDto(ItemGroup itemGroup) {
        return new ItemGroupDto(itemGroup.getItem().getId(), itemGroup.getAmount(), itemGroup.getShippingDate(), itemGroup.getBuyPrice());
    }

    public List<ItemGroupDto> toDTO(List<ItemGroup> itemGroups) {
        return itemGroups.stream().map(this::toDto).toList();
    }

    public OrderDto toDto(Order order) {
        //return items.stream().reduce(0.0, (acc, item) -> acc + item.getAmount() * item.getPrice(), Double::sum);
        double totalPrice = order.getItemGroups().stream().mapToDouble(orderGroup -> orderGroup.getBuyPrice() * orderGroup.getAmount()).sum();
        return new OrderDto(totalPrice, toDTO(order.getItemGroups()));
    }
}
