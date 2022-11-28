package com.switchfully.eurder.domain;

import com.switchfully.eurder.domain.security.Feature;
import com.switchfully.eurder.domain.security.Role;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final String id;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String phoneNumber;
    private final Adress adress;
    private final String password;
    private final Role role;

    public User(String firstname, String lastname, String email, String phoneNumber, Adress adress, String password, Role role) {
        this.password = password;
        this.id = UUID.randomUUID().toString();
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.adress = adress;
        this.role = role;
    }

    public User(String id, String firstname, String lastname, String email, String phoneNumber, Adress adress, String password, Role role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.adress = adress;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstname, user.firstname) && Objects.equals(lastname, user.lastname) && Objects.equals(email, user.email) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(adress, user.adress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, email, phoneNumber, adress);
    }

    public boolean doesPasswordMatch(String password) {
        return this.password.equals(password);
    }

    public boolean hasAccessTo(Feature feature) {
        return this.role.hasFeature(feature);
    }
}
