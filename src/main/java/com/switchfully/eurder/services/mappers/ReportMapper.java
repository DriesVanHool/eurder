package com.switchfully.eurder.services.mappers;

import com.switchfully.eurder.api.dtos.ItemGroupReportDto;
import com.switchfully.eurder.api.dtos.OrderReportDto;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.ItemGroup;
import com.switchfully.eurder.domain.Order;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportMapper {
    ItemRepository itemRepository;

    public ReportMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public OrderReportDto toDto(Order order) {
        double totalPrice = order.getItemGroups().stream().mapToDouble(orderGroup -> orderGroup.getBuyPrice() * orderGroup.getAmount()).sum();

        return new OrderReportDto(order.getId(), toDto(order.getItemGroups()), totalPrice);
    }

    public List<OrderReportDto> todDto(List<Order> orders) {
        return orders.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ItemGroupReportDto toDto(ItemGroup itemGroup) {
        String itemName = itemRepository.getItemById(itemGroup.getItemId()).map(Item::getName).orElse("Removed item");
        return new ItemGroupReportDto(itemName, itemGroup.getAmount(), itemGroup.getBuyPrice() * itemGroup.getAmount());

    }

    public List<ItemGroupReportDto> toDto(List<ItemGroup> itemGroups) {
        return itemGroups.stream().map(this::toDto).collect(Collectors.toList());
    }
}
