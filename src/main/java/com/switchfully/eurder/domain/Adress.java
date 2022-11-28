package com.switchfully.eurder.domain;

public class Adress {
    private final String street;
    private final String housenumber;
    private final String cityname;

    public Adress(String street, String housenumber, String cityname) {
        this.street = street;
        this.housenumber = housenumber;
        this.cityname = cityname;
    }
}
