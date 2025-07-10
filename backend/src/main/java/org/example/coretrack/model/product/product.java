package org.example.coretrack.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private productStatus status;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<productVariant> variants = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productGroup_id")
    private productGroup group;

    // logging elements
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updated_by;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    

    // Constructor
    public product() {
    }

    public product(String name, String sku, String description, BigDecimal price, productGroup productGroup, User createdBy) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.status = productStatus.ACTIVE;
        this.price = price;
        this.group = productGroup;
        this.created_by = createdBy;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updated_by = createdBy;
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

    public productGroup getProductGroup() { 
        return group; 
    } 
    
    public void setProductGroup(productGroup productGroup) { 
        this.group = productGroup; 
    }

    public User getCreatedBy() { 
        return created_by; 
    }

    public void setCreatedBy(User createdBy) { 
        this.created_by = createdBy; 
    }

    public User getUpdatedBy() { 
        return updated_by; 
    }

    public void setUpdatedBy(User updatedBy) { 
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
        this.updatedAt = updatedAt; }

    public boolean isActive() { 
        return isActive; 
    }

    public void setActive(boolean active) { 
        isActive = active; 
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public productStatus getStatus() {
        return status;
    }

    public void setStatus(productStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public productGroup getGroup() {
        return group;
    }

    public void setGroup(productGroup group) {
        this.group = group;
    }

    public List<productVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<productVariant> variants) {
        this.variants = variants;
    }
    
}
