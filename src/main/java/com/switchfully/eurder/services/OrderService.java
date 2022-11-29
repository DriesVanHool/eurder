package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateItemGroupDto;
import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.ItemGroup;
import com.switchfully.eurder.domain.Order;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.services.mappers.OrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    OrderRepository orderRepository;
    ItemRepository itemRepository;
    OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
    }

    public OrderDto placeOrder(List<CreateItemGroupDto> createItemGroupDtos, String userId) {

        Order order = new Order(userId, getListOfItemGroups(createItemGroupDtos));

        return orderMapper.toDto(orderRepository.save(order));
    }


    private List<ItemGroup> getListOfItemGroups(List<CreateItemGroupDto> createItemGroupDtos) {
        List<ItemGroup> itemGroups = new ArrayList<>();
        LocalDate shippingDate;

        for (CreateItemGroupDto groupItem : createItemGroupDtos) {
            shippingDate = LocalDate.now();
            Item item = itemRepository.getItemById(groupItem.itemId()).orElseThrow(() -> new NoSuchElementException("No item exists with id :" + groupItem.itemId()));
            if (item.getAmount() < groupItem.amount()) shippingDate = shippingDate.plusDays(7);
            item.setAmount(item.getAmount() - groupItem.amount());
            itemGroups.add(new ItemGroup(item.getId(), groupItem.amount(), item.getPrice(), shippingDate));
        }

        return itemGroups;
    }


}
