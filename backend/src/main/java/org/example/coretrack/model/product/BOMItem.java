package org.example.coretrack.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.UoM;
import org.example.coretrack.model.material.Material;

import jakarta.persistence.*;

@Entity
@Table(name = "BOMItem")
public class BOMItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private BOM billOfMaterials;
    
    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UoM uom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    private String notes;

    // logging fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    // Constructors
    public BOMItem() {}

    // Constructor cho Material component
    public BOMItem(BOM bom, Material material, BigDecimal quantity, UoM uom, User createdBy) {
        this.billOfMaterials = bom;
        this.material = material;
        this.quantity = quantity;
        this.uom = uom;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BOM getBillOfMaterials() {
        return billOfMaterials;
    }

    public void setBillOfMaterials(BOM billOfMaterials) {
        this.billOfMaterials = billOfMaterials;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UoM getUom() {
        return uom;
    }

    public void setUom(UoM uom) {
        this.uom = uom;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}
