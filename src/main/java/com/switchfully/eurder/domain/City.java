package com.switchfully.eurder.domain;

import javax.persistence.*;

@Entity
@Table(name = "city")
public class City {
    @Id
    private String zip;

    @Column(name= "cityname")
    private String name;

    public String getZip() {
        return zip;
    }

    public String getName() {
        return name;
    }

    public City() {
    }

    public City(String zip, String name) {
        this.zip = zip;
        this.name = name;
    }
}
