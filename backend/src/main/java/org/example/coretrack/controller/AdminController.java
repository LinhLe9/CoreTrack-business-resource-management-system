package org.example.coretrack.controller;

import org.example.coretrack.dto.auth.CreateUserRequest;
import org.example.coretrack.dto.auth.UserDetailResponse;
import org.example.coretrack.model.auth.User;
import org.example.coretrack.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            userService.createUser(createUserRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created and verification email sent successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<UserDetailResponse>> getAllUsers() {
        try {
            // Get current authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            User currentUser = (User) principal;
            List<UserDetailResponse> users = userService.getAllUsers(currentUser);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
