package org.example.coretrack.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class BOMItemRequest {

    @NotNull(message = "Material ID cannot be empty for a BOM item.")
    private Long materialId; // ID of the selected material

    @NotNull(message = "Quantity cannot be empty for a BOM item.")
    @Positive(message = "Quantity must be a positive number.")
    private BigDecimal quantity;

    // UoM is derived from Material, so not directly in the request.

    // Constructors, Getters, Setters
    public BOMItemRequest() {
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}

