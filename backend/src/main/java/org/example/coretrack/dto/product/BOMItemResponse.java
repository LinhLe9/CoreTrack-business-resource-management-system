package org.example.coretrack.dto.product;

import java.math.BigDecimal;
public class BOMItemResponse {
    private Long id;
    private Long materialId;
    private String materialName; // Display name of the material
    private BigDecimal quantity;
    private String uomDisplayName; // Display name of the Unit of Measurement
    private String notes;

    // Constructor to convert from Entity to DTO
    public BOMItemResponse(Long id, Long materialId, String materialName, BigDecimal quantity, String uomDisplayName, String notes) {
        this.id = id;
        this.materialId = materialId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.uomDisplayName = uomDisplayName;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public Long getMaterialId() { 
        return materialId; 
    }

    public void setMaterialId(Long materialId) { 
        this.materialId = materialId; 
    }

    public String getMaterialName() { 
        return materialName; 
    }

    public void setMaterialName(String materialName) { 
        this.materialName = materialName; 
    }

    public BigDecimal getQuantity() { 
        return quantity; 
    }

    public void setQuantity(BigDecimal quantity) { 
        this.quantity = quantity; 
    }

    public String getUomDisplayName() { 
        return uomDisplayName; 
    }

    public void setUomDisplayName(String uomDisplayName) { 
        this.uomDisplayName = uomDisplayName; 
    }

    public String getNotes() { 
        return notes; 
    }

    public void setNotes(String notes) { 
        this.notes = notes; 
    }
}