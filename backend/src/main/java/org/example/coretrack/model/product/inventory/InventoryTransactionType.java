package org.example.coretrack.model.product.inventory;

public enum InventoryTransactionType {
    IN("import"),  
    OUT("export"); 

    private final String displayName;

    InventoryTransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}