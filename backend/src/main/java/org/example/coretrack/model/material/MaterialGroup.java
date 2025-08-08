package org.example.coretrack.model.material;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.coretrack.model.auth.User;
import org.example.coretrack.model.auth.Company;

import jakarta.persistence.*;

@Entity
@Table(name = "MaterialGroup")
public class MaterialGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private boolean isActive;

    // map with material 
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    List<Material> materials = new ArrayList<>();

    // Multi-tenancy: Company relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

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
    public MaterialGroup() {
    }

    public MaterialGroup(String name, User created_by, Company company) {
        this.name = name;
        this.created_by = created_by;
        this.updated_by = created_by;
        this.company = company;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
