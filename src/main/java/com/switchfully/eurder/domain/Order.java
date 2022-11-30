package com.switchfully.eurder.domain;

import java.util.List;
import java.util.UUID;

public class Order {
    private final String id;
    private final String customerId;
    private final List<ItemGroup> itemGroups;

    public Order(String customerId, List<ItemGroup> orderGroups) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.itemGroups = orderGroups;
    }

    public String getId() {
        return id;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    public String getCustomerId() {
        return customerId;
    }
}
