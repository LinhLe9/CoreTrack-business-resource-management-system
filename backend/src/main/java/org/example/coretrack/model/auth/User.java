package org.example.coretrack.model.auth;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

import org.example.coretrack.model.notification.NotificationUser;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<NotificationUser> notificationUsers = new ArrayList<>();

    // Constructor
    public User(String username, String email, String password, Role role, boolean enabled, Company company) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.company = company;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String email, String password, Role role, boolean enabled, User createdBy, Company company) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdBy = createdBy;
        this.company = company;
        this.createdAt = LocalDateTime.now();
    }

    public User() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<NotificationUser> getNotificationUsers() {
        return notificationUsers;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setNotificationUsers(List<NotificationUser> notificationUsers) {
        this.notificationUsers = notificationUsers;
    }
}
