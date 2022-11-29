package com.switchfully.eurder.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String id;
    private final String customerId;
    private final LocalDate orderDate;
    private final List<ItemGroup> itemGroups;

    public Order(String customerId, List<ItemGroup> orderGroups) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.orderDate = LocalDate.now();
        this.itemGroups = orderGroups;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }
}
