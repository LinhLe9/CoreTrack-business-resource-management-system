package org.example.coretrack.dto.product;

import java.math.BigDecimal;
public class BOMItemResponse {
    private Long id;
    private String materialSku;
    private String materialName; // Display name of the material
    private BigDecimal quantity;
    private String uomDisplayName; // Display name of the Unit of Measurement

    // Constructor to convert from Entity to DTO
    public BOMItemResponse(Long id, String materialSku, String materialName, 
                        BigDecimal quantity, String uomDisplayName) {
        this.id = id;
        this.materialSku = materialSku;
        this.materialName = materialName;
        this.quantity = quantity;
        this.uomDisplayName = uomDisplayName;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
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

    public String getMaterialSku() {
        return materialSku;
    }

    public void setMaterialSku(String materialSku) {
        this.materialSku = materialSku;
    }
    
}