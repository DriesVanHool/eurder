package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {
    List<Order> orders = new ArrayList<>();

    public Order save(Order order) {
        orders.add(order);
        return order;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
