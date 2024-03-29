package com.switchfully.eurder.services;

import com.switchfully.eurder.api.dtos.*;
import com.switchfully.eurder.domain.Item;
import com.switchfully.eurder.domain.ItemGroup;
import com.switchfully.eurder.domain.Order;
import com.switchfully.eurder.domain.User;
import com.switchfully.eurder.domain.exceptions.UnauthorizedException;
import com.switchfully.eurder.domain.repositories.ItemGroupRepository;
import com.switchfully.eurder.domain.repositories.ItemRepository;
import com.switchfully.eurder.domain.repositories.OrderRepository;
import com.switchfully.eurder.domain.repositories.UserRepository;
import com.switchfully.eurder.domain.security.TokenDecoder;
import com.switchfully.eurder.services.mappers.OrderMapper;
import com.switchfully.eurder.services.mappers.ReportMapper;
import net.minidev.json.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class OrderService {
 public static final int DAYS_TO_ADD = 7;
    OrderRepository orderRepository;
    ItemRepository itemRepository;
    ItemGroupRepository itemGroupRepository;
    UserRepository userRepository;
    OrderMapper orderMapper;
    ReportMapper reportMapper;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, OrderMapper orderMapper, ReportMapper reportMapper, UserRepository userRepository, ItemGroupRepository itemGroupRepository) {
        this.orderRepository = orderRepository;
        this.itemGroupRepository = itemGroupRepository;
        this.orderMapper = orderMapper;
        this.reportMapper = reportMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public OrderDto placeOrder(List<CreateItemGroupDto> createItemGroupDtos, String authorization) throws ParseException {
        String emailToken = TokenDecoder.tokenDecode(authorization);
        User user =  userRepository.getUserByEmail(emailToken).stream().findFirst().orElseThrow(UnauthorizedException::new);

        Order order = new Order(user);
        List<ItemGroup> itemGroups = getListOfItemGroups(createItemGroupDtos, order);
        order.setItemGroups(itemGroups);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private List<ItemGroup> getListOfItemGroups(List<CreateItemGroupDto> createItemGroupDtos, Order order) throws NoSuchElementException {
        List<ItemGroup> itemGroups = new ArrayList<>();
        LocalDate shippingDate;

        for (CreateItemGroupDto groupItem : createItemGroupDtos) {
            int itemId = checkInt(groupItem.itemId());
            int itemAmount = checkInt(groupItem.amount());
            shippingDate = LocalDate.now().plusDays(1);
            Item item = itemRepository.getItemsById(itemId).stream().findFirst().orElseThrow(() -> new NoSuchElementException("No item exists with id :" + groupItem.itemId()));
            item.setAmount(item.getAmount() - itemAmount);
            if (item.getAmount() == 0) shippingDate = LocalDate.now().plusDays(DAYS_TO_ADD);

            itemGroups.add(itemGroupRepository.save(new ItemGroup(item, order, itemAmount, item.getPrice(), shippingDate)));
        }

        return itemGroups;
    }

    public TotalOrderReportDto getOrderReport(String authorization)throws ParseException {
        String emailToken = TokenDecoder.tokenDecode(authorization);
        User user =  userRepository.getUserByEmail(emailToken).stream().findFirst().orElseThrow(UnauthorizedException::new);

        List<OrderReportDto> orderReports = reportMapper.todDto(getOrdersByUserId(user.getId()));
        double totalPrice = orderReports.stream().mapToDouble(OrderReportDto::orderPrice).sum();
        return new TotalOrderReportDto(orderReports, totalPrice);
    }


    public List<Order> getOrdersByUserId(int id) {
        return orderRepository.findAll().stream().filter(order -> order.getUser().getId()== id).toList();
    }

    public OrderDto reorderOrder(String orderId, String authorization) throws ParseException, UnauthorizedException {
        String emailToken = TokenDecoder.tokenDecode(authorization);
        User user =  userRepository.getUserByEmail(emailToken).stream().findFirst().orElseThrow(UnauthorizedException::new);

        int id = checkInt(orderId);

        Order order = orderRepository.getOrderById(id).stream().findFirst().orElseThrow(() -> new NoSuchElementException("No order found with id: " + orderId));
        if (!order.getUser().getEmail().equals(user.getEmail()))
        {
            throw new IllegalArgumentException("This is not your order");
        }
        List<CreateItemGroupDto> createItemGroupDtos = new ArrayList<>();
        for (ItemGroup itemGroup : order.getItemGroups()) {
            createItemGroupDtos.add(new CreateItemGroupDto(String.valueOf(itemGroup.getItem().getId()), String.valueOf(itemGroup.getAmount())));
        }
        return placeOrder(createItemGroupDtos, authorization);
    }

    public int checkInt(String intToCheck){
        int id;

        try {
            id = Integer.parseInt(intToCheck);
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("Invalid int value");
        }
        return id;
    }
}
