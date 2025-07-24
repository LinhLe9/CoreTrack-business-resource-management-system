package org.example.coretrack.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class BOMItemRequest {

    @NotNull(message = "Material SKU cannot be empty for a BOM item.")
    private String materialSku; // ID of the selected material

    @NotNull(message = "Quantity cannot be empty for a BOM item.")
    @Positive(message = "Quantity must be a positive number.")
    private BigDecimal quantity;

    // UoM is derived from Material, so not directly in the request.

    // Constructors, Getters, Setters
    public BOMItemRequest() {
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }


    public String getMaterialSku() {
        return materialSku;
    }


    public void setMaterialSku(String materialSku) {
        this.materialSku = materialSku;
    }
}

