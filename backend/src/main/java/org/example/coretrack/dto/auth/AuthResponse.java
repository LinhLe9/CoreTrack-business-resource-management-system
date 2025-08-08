package org.example.coretrack.dto.auth;

import org.example.coretrack.model.auth.Role;

public class AuthResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;

    // All-argument constructor
    public AuthResponse(String token, Long id, String username, String email, Role role, boolean enabled) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
