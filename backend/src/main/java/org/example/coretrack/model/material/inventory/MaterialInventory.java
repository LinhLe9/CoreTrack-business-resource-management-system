package org.example.coretrack.model.material.inventory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.MaterialVariant;
import jakarta.persistence.*;

@Entity
@Table(name = "MaterialInventory")
public class MaterialInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = MaterialVariant.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "materialVariant_id")
    private MaterialVariant materialVariant;

    private Integer currentStock;
    private Integer minAlertStock;
    private Integer maxStockLevel;

    @OneToMany(mappedBy = "materialInventory", cascade = CascadeType.ALL)
    List<MaterialInventoryLog> logs = new ArrayList<>();

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

    public MaterialInventory(){
    }

    public MaterialInventory(MaterialVariant materialVariant, Integer currentStock, Integer minAlertStock, Integer maxStockLevel,
            List<MaterialInventoryLog> logs, User created_by) {
        this.materialVariant = materialVariant;
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

    public MaterialVariant getMaterialVariant() {
        return materialVariant;
    }

    public void setMaterialVariant(MaterialVariant materialVariant) {
        this.materialVariant = materialVariant;
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

    public List<MaterialInventoryLog> getLogs() {
        return logs;
    }

    public void setLogs(List<MaterialInventoryLog> logs) {
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
