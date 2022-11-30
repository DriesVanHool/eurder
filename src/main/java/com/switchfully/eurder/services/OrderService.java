package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.CreateItemGroupDto;
import com.switchfully.eurder.api.dtos.OrderDto;
import com.switchfully.eurder.api.dtos.OrderReportDto;
import com.switchfully.eurder.api.dtos.TotalOrderReportDto;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.ItemGroup;
import com.switchfully.eurder.domain.Order;
import com.switchfully.eurder.domain.exceptions.UnauthorizedException;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.services.mappers.OrderMapper;
import com.switchfully.eurder.services.mappers.ReportMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    public static final int DAYS_TO_ADD = 7;
    OrderRepository orderRepository;
    ItemRepository itemRepository;
    OrderMapper orderMapper;
    ReportMapper reportMapper;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, OrderMapper orderMapper, ReportMapper reportMapper) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.reportMapper = reportMapper;
    }

    public OrderDto placeOrder(List<CreateItemGroupDto> createItemGroupDtos, String userId) throws NoSuchElementException {
        Order order = new Order(userId, getListOfItemGroups(createItemGroupDtos));
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto reorderOrder(String orderId, String userId) throws RuntimeException {
        Order order = orderRepository.getOrderById(orderId).orElseThrow(() -> new NoSuchElementException("No order found with id: " + orderId));
        if (!order.getCustomerId().equals(userId))
            throw new UnauthorizedException();
        List<CreateItemGroupDto> createItemGroupDtos = new ArrayList<>();
        for (ItemGroup itemGroup : order.getItemGroups()) {
            createItemGroupDtos.add(new CreateItemGroupDto(itemGroup.getItemId(), itemGroup.getAmount()));
        }
        return placeOrder(createItemGroupDtos, userId);

    }

    public TotalOrderReportDto getOrderReport(String userId) {
        List<OrderReportDto> orderReports = reportMapper.todDto(getOrdersByUserId(userId));
        double totalPrice = orderReports.stream().mapToDouble(OrderReportDto::orderPrice).sum();
        return new TotalOrderReportDto(orderReports, totalPrice);
    }


    private List<ItemGroup> getListOfItemGroups(List<CreateItemGroupDto> createItemGroupDtos) throws NoSuchElementException {
        List<ItemGroup> itemGroups = new ArrayList<>();
        LocalDate shippingDate;

        for (CreateItemGroupDto groupItem : createItemGroupDtos) {
            shippingDate = LocalDate.now().plusDays(1);
            Item item = itemRepository.getItemById(groupItem.itemId()).orElseThrow(() -> new NoSuchElementException("No item exists with id :" + groupItem.itemId()));
            item.setAmount(item.getAmount() - groupItem.amount());
            if (item.getAmount() == 0) shippingDate = LocalDate.now().plusDays(DAYS_TO_ADD);

            itemGroups.add(new ItemGroup(item.getId(), item.getName(), groupItem.amount(), item.getPrice(), shippingDate));
        }

        return itemGroups;
    }

    public List<Order> getOrdersByUserId(String id) {
        return orderRepository.getOrders().stream().filter(order -> order.getCustomerId().equals(id)).toList();
    }
}
