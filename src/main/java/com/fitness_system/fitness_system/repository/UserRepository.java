package com.fitness_system.fitness_system.repository;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness_system.fitness_system.models.User;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private static final String FILE_PATH = "users.txt";

    public List<User> findAll() {
        File file = new File(FILE_PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = new ArrayList<>();
        try {
            if (file.exists() && file.length() > 0) {
                users = objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User save(User user) {
        List<User> users = findAll();
        if (user.getId() == 0) { // New user
            long newId = users.stream().mapToLong(User::getId).max().orElse(0) + 1;
            user.setId(newId);
            users.add(user);
        } else { // Update existing user
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == user.getId()) {
                    users.set(i, user);
                    break;
                }
            }
        }
        saveToFile(users);
        return user;
    }

    public User findById(long id) {
        List<User> users = findAll();
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public void deleteById(long id) {
        List<User> users = findAll();
        boolean removed = users.removeIf(user -> user.getId() == id);
        if (!removed) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        saveToFile(users);
    }

    public User findByUsername(String username) {
        List<User> users = findAll();
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    private void saveToFile(List<User> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(FILE_PATH), users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}