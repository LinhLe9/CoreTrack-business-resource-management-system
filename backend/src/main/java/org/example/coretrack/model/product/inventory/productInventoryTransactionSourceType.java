package org.example.coretrack.model.product.inventory;

public enum ProductInventoryTransactionSourceType {
    // Inflow Transactions
    PURCHASE_ORDER_RECEIPT("Purchase Order Receipt"),
    PRODUCTION_COMPLETION("Production Completion"),
    CUSTOMER_RETURN("Customer Return"),
    PRODUCT_ADJUSTMENT_INCREASE("Inventory Adjustment Increase"),
    PRODUCT_WAREHOUSE_TRANSFER_IN("Warehouse Transfer In"),

    // Outflow Transactions
    SALES_ORDER_FULFILLMENT("Sales Order Fulfillment"),
    PRODUCTION_CONSUMPTION("Production Consumption"),
    SUPPLIER_RETURN("Supplier Return"),
    INVENTORY_ADJUSTMENT_DECREASE("Inventory Adjustment Decrease"),
    WAREHOUSE_TRANSFER_OUT("Warehouse Transfer Out"),
    SCRAP("Product Scrap");

    private final String displayName;

    ProductInventoryTransactionSourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
