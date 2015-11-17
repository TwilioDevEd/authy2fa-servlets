package com.twilio.authy2fa.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "authy_id")
    private String authyId;

    @Column(name = "authy_status")
    private String authyStatus;

    @Column(name = "date")
    private Date date;

    public User() {
    }

    public User(
            String name,
            String email,
            String password,
            String countryCode,
            String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.authyId = "";
        this.authyStatus = "";
        this.date = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAuthyId() {
        return authyId;
    }

    public void setAuthyId(String authyId) {
        this.authyId = authyId;
    }

    public String getAuthyStatus() {
        return authyStatus;
    }

    public void setAuthyStatus(String authyStatus) {
        this.authyStatus = authyStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
