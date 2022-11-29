package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    ItemGroupMapper itemGroupMapper;

    public OrderMapper(ItemGroupMapper itemGroupMapper) {
        this.itemGroupMapper = itemGroupMapper;
    }

    public OrderDto toDto(Order order) {
        double totalPrice = order.getItemGroups().stream().mapToDouble(orderGroup -> orderGroup.getBuyPrice() * orderGroup.getAmount()).sum();

        return new OrderDto(totalPrice, itemGroupMapper.toDTO(order.getItemGroups()));
    }
}
