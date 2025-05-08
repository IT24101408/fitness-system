package com.fitness_system.fitness_system.models;



public class UserAdmin extends User {
    public UserAdmin() {
        setRole("ADMIN");
    }

    public UserAdmin(long id, String username, String password) {
        super(id, username, password, "ADMIN");
    }
}