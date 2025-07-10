package org.example.coretrack.model.supplier;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable 
public class MaterialSupplierId implements Serializable {

    private Long materialId; 
    private Long supplierId;

    // Constructors
    public MaterialSupplierId() {}

    public MaterialSupplierId(Long materialId, Long supplierId) {
        this.materialId = materialId;
        this.supplierId = supplierId;
    }

    // Getters and Setters
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    // Quan trọng: Phải override equals() và hashCode() cho khóa chính tổng hợp
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialSupplierId that = (MaterialSupplierId) o;
        return Objects.equals(materialId, that.materialId) &&
               Objects.equals(supplierId, that.supplierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, supplierId);
    }
}

