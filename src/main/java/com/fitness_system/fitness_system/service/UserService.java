package com.fitness_system.fitness_system.service;



import com.fitness_system.fitness_system.models.User;
import com.fitness_system.fitness_system.models.UserAdmin;
import com.fitness_system.fitness_system.models.UserCustomer;
import com.fitness_system.fitness_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String username, String password, String role) {
        if (username == null || username.isBlank() || password == null || password.isBlank() || role == null || role.isBlank()) {
            throw new IllegalArgumentException("Username, password, and role cannot be empty");
        }
        // Check if username already exists
        List<User> users = userRepository.findAll();
        if (users.stream().anyMatch(user -> user.getUsername().equals(username))) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = role.equalsIgnoreCase("ADMIN") ? new UserAdmin() : new UserCustomer();
        user.setUsername(username);
        user.setPassword(password); // Plain text, no hashing for simplicity
        user.setRole(role.toUpperCase());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        return userRepository.findById(id);
    }

    public User updateUser(long id, String username, String password, String role) {
        User user = userRepository.findById(id);
        if (username != null && !username.isBlank()) {
            // Check if new username is taken by another user
            List<User> users = userRepository.findAll();
            if (users.stream().anyMatch(u -> u.getUsername().equals(username) && u.getId() != id)) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(username);
        }
        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }
        if (role != null && !role.isBlank()) {
            user.setRole(role.toUpperCase());
            // Update instance type if role changes
            if (role.equalsIgnoreCase("ADMIN") && !(user instanceof UserAdmin)) {
                User newUser = new UserAdmin();
                newUser.setId(user.getId());
                newUser.setUsername(user.getUsername());
                newUser.setPassword(user.getPassword());
                newUser.setRole("ADMIN");
                user = newUser;
            } else if (role.equalsIgnoreCase("CUSTOMER") && !(user instanceof UserCustomer)) {
                User newUser = new UserCustomer();
                newUser.setId(user.getId());
                newUser.setUsername(user.getUsername());
                newUser.setPassword(user.getPassword());
                newUser.setRole("CUSTOMER");
                user = newUser;
            }
        }
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.findById(id); // Will throw if not found
        userRepository.deleteById(id);
    }

    public User authenticateUser(String username, String password) {
        try {
            User user = userRepository.findByUsername(username);
            if (user.getPassword().equals(password)) {
                return user;
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}