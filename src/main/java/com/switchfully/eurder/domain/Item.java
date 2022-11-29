package com.switchfully.eurder.domain;

import java.util.UUID;

public class Item {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private final int amount;
    private final StockLvl stockLvl;

    public Item(String name, String description, double price, int amount) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.price = price;
        this.amount = amount;
        this.stockLvl = calculateStockLvl(amount);
    }

    private StockLvl calculateStockLvl(int amount) {
        if (amount < 5) return StockLvl.STOCK_LOW;
        if (amount < 10) return StockLvl.STOCK_MEDIUM;
        return StockLvl.STOCK_HIGH;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public StockLvl getStockLvl() {
        return stockLvl;
    }
}