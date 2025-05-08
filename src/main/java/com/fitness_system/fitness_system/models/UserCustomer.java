package com.fitness_system.fitness_system.models;


public class UserCustomer extends User {
    public UserCustomer() {
        setRole("CUSTOMER");
    }

    public UserCustomer(long id, String username, String password) {
        super(id, username, password, "CUSTOMER");
    }
}