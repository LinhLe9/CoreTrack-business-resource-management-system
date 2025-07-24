package org.example.coretrack.model.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;

import jakarta.persistence.*;

@Entity
@Table(name = "ProductGroup")
public class ProductGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private boolean isActive;

    // map with product
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    List<Product> products = new ArrayList<>();

    //logging element
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updated_by;

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    
    // constructor
    public ProductGroup() {
    }

    public ProductGroup(String name, User created_by) {
        this.name = name;
        this.created_by = created_by;
        this.updated_by = created_by;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public User getCreated_by() { 
        return created_by; 
    }

    public void setCreated_by(User createdBy) { 
        this.created_by = createdBy; 
    }

    public User getUpdated_by() { 
        return updated_by; 
    }

    public void setUpdated_by(User updatedBy) { 
        this.updated_by = updatedBy; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }

    public boolean getIsActive() { 
        return isActive; 
    }

    public void setActive(boolean active) { 
        isActive = active; 
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}