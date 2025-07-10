package org.example.coretrack.model.supplier;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.Material;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_supplier")
public class MaterialSupplier {
    @EmbeddedId 
    private MaterialSupplierId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("materialId") 
    @JoinColumn(name = "material_id") 
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("supplierId") 
    @JoinColumn(name = "supplier_id") 
    private supplier supplier;

    // import price
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price; 

    // price currency
    @Column(length = 3, nullable = false)
    private String currency; 

    // delivery time (days)
    private Integer leadTimeDays;

    // minimum number of products
    private Integer minOrderQuantity; 

    @Column(unique = true) 
    private String supplierMaterialCode;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    // Constructors
    public MaterialSupplier() {}

    public MaterialSupplier(Material material, supplier supplier, BigDecimal price, String currency, User createdBy) {
        this.material = material;
        this.supplier = supplier;
        this.id = new MaterialSupplierId(material.getId(), supplier.getId()); // Khởi tạo khóa chính tổng hợp
        this.price = price;
        this.currency = currency;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = createdBy;
    }

    public MaterialSupplierId getId() {
        return id;
    }

    public void setId(MaterialSupplierId id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(supplier supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getLeadTimeDays() {
        return leadTimeDays;
    }

    public void setLeadTimeDays(Integer leadTimeDays) {
        this.leadTimeDays = leadTimeDays;
    }

    public Integer getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(Integer minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public String getSupplierMaterialCode() {
        return supplierMaterialCode;
    }

    public void setSupplierMaterialCode(String supplierMaterialCode) {
        this.supplierMaterialCode = supplierMaterialCode;
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
