package com.example.myserver.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a user with optional admin rights
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid User user, @RequestParam(required = false) String adminSecret) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        final String ADMIN_SECRET = "secret";  // This should be managed securely
        if (ADMIN_SECRET.equals(adminSecret)) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("User registered successfully with ID: " + savedUser.getId() + " and Role: " + savedUser.getRole());
    }

    // Fetch a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Principal principal) {
        return userRepository.findById(id).map(user -> {
            if (!principal.getName().equals(user.getUsername()) && !"ADMIN".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    // Update a user's details selectively
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates, Principal principal) {
        return userRepository.findById(id).map(user -> {
            if (!principal.getName().equals(user.getUsername()) && !"ADMIN".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            updates.forEach((key, value) -> {
                switch (key) {
                    case "username": user.setUsername((String) value); break;
                    case "email": user.setEmail((String) value); break;
                    case "profileDescription": user.setProfileDescription((String) value); break;
                    case "password": user.setPassword(passwordEncoder.encode((String) value)); break;
                }
            });
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    // Admin grants admin role
    @PutMapping("/admin/{id}/grant-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> grantAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setRole("ADMIN");
        userRepository.save(user);
        return ResponseEntity.ok("Role updated to ADMIN for user: " + user.getUsername());
    }

    // Admin revokes admin role
    @PutMapping("/admin/{id}/revoke-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> revokeAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.ok("Role updated to USER for user: " + user.getUsername());
    }

    // Delete a user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
