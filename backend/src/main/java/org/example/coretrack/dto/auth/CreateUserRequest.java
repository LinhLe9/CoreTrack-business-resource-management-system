package org.example.coretrack.dto.auth;

import org.example.coretrack.model.auth.Role;

public class CreateUserRequest {
    private String email;
    private Role role;

    // Getters and setters
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
}
