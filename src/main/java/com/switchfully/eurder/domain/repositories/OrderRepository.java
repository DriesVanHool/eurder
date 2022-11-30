package com.switchfully.eurder.domain.repositories;

import com.switchfully.eurder.domain.Order;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<Order> getOrderById(String id) {
        return orders.stream().filter(order -> order.getId().equals(id)).findFirst();
    }
}
