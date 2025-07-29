package org.example.coretrack.model.material;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.supplier.MaterialSupplier;

import jakarta.persistence.*;

@Entity
@Table(name = "Material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String shortDes;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated
    @Column(nullable = false)
    private MaterialStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UoM uom;

    @Column(length = 10000)
    private String imageUrl;


    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    private List<MaterialVariant> variants = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialGroup_id")
    private MaterialGroup group;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MaterialSupplier> materialSuppliers = new HashSet<>();

    // logging elements
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updated_by;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Material(String sku, String name, String shortDes, UoM uom, String imageUrl,
            List<MaterialVariant> variants, MaterialGroup group, User created_by) {
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.isActive = true;
        this.status = MaterialStatus.ACTIVE;
        this.uom = uom;
        this.imageUrl = imageUrl;
        this.variants = variants;
        this.group = group;
        this.created_by = created_by;
        this.updated_by = created_by;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Material(String sku, String name, String shortDes,
        MaterialGroup group, User created_by) {
        this.sku = sku;
        this.name = name;
        this.shortDes = shortDes;
        this.isActive = true;
        this.status = MaterialStatus.ACTIVE;
        this.group = group;
        this.created_by = created_by;
        this.updated_by = created_by;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public Material(){
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

    public List<MaterialVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<MaterialVariant> variants) {
        this.variants = variants;
    }

    public MaterialGroup getGroup() {
        return group;
    }

    public void setGroup(MaterialGroup group) {
        this.group = group;
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

    public Set<MaterialSupplier> getMaterialSuppliers() {
        return materialSuppliers;
    }

    public void setMaterialSuppliers(Set<MaterialSupplier> materialSuppliers) {
        this.materialSuppliers = materialSuppliers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
}
