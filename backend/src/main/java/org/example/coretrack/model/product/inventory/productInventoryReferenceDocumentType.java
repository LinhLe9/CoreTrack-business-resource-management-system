package org.example.coretrack.model.product.inventory;

public enum ProductInventoryReferenceDocumentType {
    PURCHASE_ORDER("Purchase Order"),
    SALES_ORDER("Sales Order"),
    PRODUCTION_ORDER("Production Order"),
    PRODUCTION_TICKET("Production Ticket"), 
    INVENTORY_ADJUSTMENT("Inventory Adjustment"),
    WAREHOUSE_TRANSFER("Warehouse Transfer");

    private final String displayName;

    ProductInventoryReferenceDocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
