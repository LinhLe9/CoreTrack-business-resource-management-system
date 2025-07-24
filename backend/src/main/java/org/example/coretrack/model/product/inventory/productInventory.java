package org.example.coretrack.model.product.inventory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.product.ProductVariant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "ProductInventory")
public class ProductInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(targetEntity = ProductVariant.class, cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JoinColumn(name = "productVariant_id")
    private ProductVariant productVariant;

    private Integer currentStock;
    private Integer minAlertStock;
    private Integer maxStockLevel;

    @OneToMany(mappedBy = "productInventory", cascade = CascadeType.ALL)
    private List<ProductInventoryLog> logs = new ArrayList<>();

    @Column(nullable = false)
    private boolean isActive;

    // logging elements
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updated_by;

    public ProductInventory() {
    }

    public ProductInventory(ProductVariant productVariant, Integer currentStock, Integer minAlertStock, Integer maxStockLevel,
            List<ProductInventoryLog> logs, User created_by) {
        this.productVariant = productVariant;
        this.currentStock = currentStock;
        this.minAlertStock = minAlertStock;
        this.isActive = true;
        this.maxStockLevel =maxStockLevel;
        this.logs = logs;
        this.created_by = created_by;
        this.updated_by = created_by;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getMinAlertStock() {
        return minAlertStock;
    }

    public void setMinAlertStock(Integer minAlertStock) {
        this.minAlertStock = minAlertStock;
    }

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public List<ProductInventoryLog> getLogs() {
        return logs;
    }

    public void setLogs(List<ProductInventoryLog> logs) {
        this.logs = logs;
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
}
