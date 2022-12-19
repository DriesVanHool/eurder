package com.switchfully.eurder.domain;

import com.switchfully.eurder.domain.security.Role;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = "person_seq", allocationSize = 1)
    private int id;

    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "email")
    private String email;
    @Column(name = "phonenumber")
    private String phoneNumber;

    @Embedded
    private Adress adress;

    @ManyToOne
    @JoinColumn(name = "roleid")
    private Role role;

    public User(String firstname, String lastname, String email, String phoneNumber, Adress adress, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.adress = adress;
        this.role = role;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Adress getAdress() {
        return adress;
    }

    public Role getRole() {
        return role;
    }
}
