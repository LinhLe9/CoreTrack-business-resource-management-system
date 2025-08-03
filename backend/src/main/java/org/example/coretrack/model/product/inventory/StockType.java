package org.example.coretrack.model.product.inventory;

public enum StockType {
    CURRENT("Current Stock"),
    ALLOCATED("Allocated Stock"),
    FUTURE ("Future Stock");

    private final String displayName;

    StockType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
