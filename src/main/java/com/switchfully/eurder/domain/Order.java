package com.switchfully.eurder.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "orderline")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    private int id;
    @ManyToOne
    @JoinColumn(name = "personid")
    private User user;

    @Column(name = "orderdate")
    private LocalDate orderdate;

    @OneToMany
    @JoinColumn(name = "orderid")
    private List<ItemGroup> itemGroups;

    public Order(User user, List<ItemGroup> itemGroups) {
        this.user = user;
        this.itemGroups = itemGroups;
        this.orderdate = LocalDate.now();
    }

    public void setItemGroups(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    public Order(User user) {
        this.user = user;
        this.orderdate = LocalDate.now();
    }

    public Order() {
        this.orderdate = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getOrderdate() {
        return orderdate;
    }
}
