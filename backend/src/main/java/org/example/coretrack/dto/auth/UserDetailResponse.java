package org.example.coretrack.dto.auth;

import org.example.coretrack.model.auth.Role;
import java.time.LocalDateTime;

public class UserDetailResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private String createdByEmail; // Email của người tạo
    private String createdByUsername; // Username của người tạo
    private LocalDateTime createdAt;

    // Constructor
    public UserDetailResponse() {}

    public UserDetailResponse(Long id, String username, String email, Role role, boolean enabled, 
                           String createdByEmail, String createdByUsername, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdByEmail = createdByEmail;
        this.createdByUsername = createdByUsername;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
