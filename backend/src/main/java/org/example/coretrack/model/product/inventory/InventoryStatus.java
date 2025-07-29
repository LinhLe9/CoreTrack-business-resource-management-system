package org.example.coretrack.model.product.inventory;

public enum InventoryStatus {
    OUT_OF_STOCK("Out of Stock"),
    IN_STOCK("In Stock"), 
    LOW_STOCK("Low Stock"), 
    OVER_STOCK("Over Stock");

    private final String displayName;

    InventoryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
