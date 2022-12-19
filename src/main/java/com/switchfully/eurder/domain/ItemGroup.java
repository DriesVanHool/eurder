package com.switchfully.eurder.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "itemgroup")
public class ItemGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemgroup_seq")
    @SequenceGenerator(name = "itemgroup_seq", sequenceName = "itemgroup_seq", allocationSize = 1)
    private  int id;

    @ManyToOne
    @JoinColumn(name = "itemid")
    private Item item;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "orderid")
    private Order order;

    @Column(name = "amount")
    private int amount;

    @Column(name = "buyprice")
    private double buyPrice;

    @Column(name = "shippingdate")
    private LocalDate shippingDate;

    public ItemGroup(Item item, Order order, int amount, double buyPrice, LocalDate shippingDate) {
        this.item = item;
        this.order = order;
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.shippingDate = shippingDate;
    }

    public ItemGroup() {
    }

    public int getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }
}
