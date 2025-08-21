package com.resume.matcher.service;


import com.resume.matcher.models.Role;
import com.resume.matcher.models.User;
import com.resume.matcher.repository.RoleRepository;
import com.resume.matcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        Role normalRole = roleRepository.findById(2L) // ✅ Assign only NORMAL role (ID = 2)
                .orElseThrow(() -> new RuntimeException("Role with ID 2 not found"));

        user.setRoles(Collections.singleton(normalRole));  // ✅ Only assigning NORMAL role
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get User by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
