package com.switchfully.eurder.domain;

import javax.persistence.*;

@Embeddable
public class Adress {
    @Column(name = "street")
    private String street;

    @Column(name = "housenumber")
    private String houseNumber;

    @ManyToOne
    @JoinColumn(name = "zip")
    private City city;

    public Adress() {
    }

    public Adress(String street, String houseNumber, City city) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public City getCity() {
        return city;
    }
}
