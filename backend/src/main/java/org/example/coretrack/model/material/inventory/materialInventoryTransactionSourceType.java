package org.example.coretrack.model.material.inventory;

public enum materialInventoryTransactionSourceType {
    // in_flow
    PURCHASE_RECEIPT("Material Purchase Receipt"),
    PRODUCTION_RETURN("Material Return From Production"), // Vật liệu thừa từ sản xuất trả lại kho
    MATERIAL_ADJUSTMENT_INCREASE("Material Adjustment Increase"),
    MATERIAL_WAREHOUSE_TRANSFER_IN("Material Warehouse Transfer In"),

    // out_flow
    PRODUCTION_CONSUMPTION("Material Production Consumption"),
    SUPPLIER_RETURN("Material Supplier Return"),
    MATERIAL_ADJUSTMENT_DECREASE("Material Adjustment Decrease"),
    MATERIAL_WAREHOUSE_TRANSFER_OUT("Material Warehouse Transfer Out"),
    SCRAP("Material Scrap"),

    // Set
    SET_INVENTORY("Set Stock Inventory");

    private final String displayName;

    materialInventoryTransactionSourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}