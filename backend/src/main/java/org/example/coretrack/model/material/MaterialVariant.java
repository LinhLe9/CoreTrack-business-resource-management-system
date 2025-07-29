package org.example.coretrack.model.material;

import java.time.LocalDateTime;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.material.inventory.MaterialInventory;

import jakarta.persistence.*;

@Entity
@Table(name = "MaterialVariant")
public class MaterialVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 16)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String shortDes;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UoM uom;

    @Column(length = 10000)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;  

    
    @OneToOne(mappedBy = "materialVariant", fetch = FetchType.LAZY)
    private MaterialInventory materialInventory;

    // logging elements
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updated_by;

    public MaterialVariant(String sku, String name, String shortDes, UoM uom, String imageUrl,
                            Material material, User created_by) {
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.isActive = true;
        this.status = MaterialStatus.ACTIVE;
        this.uom = uom;
        this.imageUrl = imageUrl;
        this.material = material;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.created_by = created_by;
        this.updated_by = created_by;
    }

    public MaterialVariant(String sku, String name, String shortDes, UoM uom,
                            Material material, User created_by) {
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.isActive = true;
        this.status = MaterialStatus.ACTIVE;
        this.uom = uom;
        this.material = material;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.created_by = created_by;
        this.updated_by = created_by;
    }

    public MaterialVariant(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDes() {
        return shortDes;
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public MaterialStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialStatus status) {
        this.status = status;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MaterialInventory getMaterialInventory() {
        return materialInventory;
    }

    public void setMaterialInventory(MaterialInventory materialInventory) {
        this.materialInventory = materialInventory;
    }
    
}

