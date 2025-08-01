package org.example.coretrack.model.material.inventory;

public enum materialInventoryReferenceDocumentType {
    PRODUCTION_TICKET("Production ticket"),
    PURCHASING_TICKET("Purchasing ticket"), 
    INVENTORY_ADJUSTMENT("Inventory adjustment"),
    WAREHOUSE_TRANSFER("Warehouse Transfer");

    private final String displayName;

    materialInventoryReferenceDocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
