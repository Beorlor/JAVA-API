package com.example.myserver.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

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

        final String ADMIN_SECRET = "secret";  // Ensure this is securely managed
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
        User currentUser = userRepository.findByUsername(principal.getName());
        User targetUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!currentUser.getUsername().equals(targetUser.getUsername()) && !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(targetUser);
    }

    // Update a user's details selectively
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        User userToUpdate = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!currentUser.getUsername().equals(userToUpdate.getUsername()) && !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Apply updates selectively
        updates.forEach((key, value) -> {
            switch (key) {
                case "username":
                    userToUpdate.setUsername((String) value);
                    break;
                case "email":
                    userToUpdate.setEmail((String) value);
                    break;
                case "profileDescription":
                    userToUpdate.setProfileDescription((String) value);
                    break;
                case "password":
                    userToUpdate.setPassword(passwordEncoder.encode((String) value));
                    break;
            }
        });

        userRepository.save(userToUpdate);
        return ResponseEntity.ok("User updated successfully");
    }

    // Admin grants admin role
    @PutMapping("/admin/{id}/grant-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> grantAdmin(@PathVariable Long id) {
        return changeUserRole(id, "ADMIN");
    }

    // Admin revokes admin role
    @PutMapping("/admin/{id}/revoke-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> revokeAdmin(@PathVariable Long id) {
        return changeUserRole(id, "USER");
    }

    // Helper method to change user role
    private ResponseEntity<?> changeUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok("Role updated to " + role + " for user: " + user.getUsername());
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!currentUser.getUsername().equals(user.getUsername()) && !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userRepository.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }
}
