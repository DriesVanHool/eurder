package com.switchfully.eurder.domain;

import java.time.LocalDate;

public class ItemGroup {
    private final String itemId;
    private final int amount;
    private final double buyPrice;
    private final LocalDate shippingDate;


    public ItemGroup(String itemId, int amount, double buyPrice, LocalDate shippingDate) {
        this.itemId = itemId;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.shippingDate = shippingDate;
    }

    public String getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }
}
