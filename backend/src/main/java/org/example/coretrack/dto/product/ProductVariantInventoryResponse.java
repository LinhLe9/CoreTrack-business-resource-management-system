package org.example.coretrack.dto.product;

public class ProductVariantInventoryResponse {
    private ProductVariantInfoResponse variant;
    private InventoryResponse inventory;

    public ProductVariantInventoryResponse(ProductVariantInfoResponse variant, InventoryResponse inventory) {
        this.variant = variant;
        this.inventory = inventory;
    }

    public ProductVariantInventoryResponse(){}

    public ProductVariantInfoResponse getVariant() {
        return variant;
    }

    public void setVariant(ProductVariantInfoResponse variant) {
        this.variant = variant;
    }

    public InventoryResponse getInventory() {
        return inventory;
    }

    public void setInventory(InventoryResponse inventory) {
        this.inventory = inventory;
    }
}
