package org.example.coretrack.model.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;

import jakarta.persistence.*;

@Entity
@Table(name = "billOfMaterial")
public class BOM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "productVariant_id", unique = true, nullable = false)
    private productVariant productVariant;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "billOfMaterials", cascade = CascadeType.ALL)
    private List<BOMItem> bomItems = new ArrayList<>();

    // logging elements
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updated_by;

    // constructor 
    public BOM() {
    }

    

    public BOM(productVariant productVariant, String description, User created_by) {
        this.productVariant = productVariant;
        this.description = description;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.created_by = created_by;
        this.updated_by = created_by;
    }

    // getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public productVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(productVariant productVariant) {
        this.productVariant = productVariant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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

    public User getCreated_by() {
        return created_by;
    }

    public void setCreated_by(User created_by) {
        this.created_by = created_by;
    }

    public User getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(User updated_by) {
        this.updated_by = updated_by;
    }



    public List<BOMItem> getBomItems() {
        return bomItems;
    }



    public void setBomItems(List<BOMItem> bomItems) {
        this.bomItems = bomItems;
    }
}
