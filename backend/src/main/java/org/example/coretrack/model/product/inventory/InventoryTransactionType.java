package org.example.coretrack.model.product.inventory;

public enum InventoryTransactionType {
    IN("import"),  
    OUT("export"), 
    SET("set exact stock");

    private final String displayName;

    InventoryTransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}