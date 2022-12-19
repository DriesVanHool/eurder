package com.switchfully.eurder.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "item_seq", allocationSize = 1)
    private int id;
    @Column(name = "itemname")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private double price;

    @Column(name = "amount")
    private int amount;

    @Transient
    private StockLvl stockLvl;

    public Item() {
        this.stockLvl = calculateStockLvl(amount);
    }

    public Item(String name, String description, double price, int amount) {
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

    public int getId() {
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
        return calculateStockLvl(this.amount);
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            this.amount = 0;
        } else {
            this.amount = amount;
        }
        this.stockLvl = calculateStockLvl(this.amount);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
