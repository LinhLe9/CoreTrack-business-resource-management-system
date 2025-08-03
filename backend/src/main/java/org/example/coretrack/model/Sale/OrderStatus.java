package org.example.coretrack.model.Sale;

public enum OrderStatus {
    NEW("New"),
    ALLOCATED ("Allocated"),
    PACKED("Packed"),
    SHIPPED("Shipped"),
    DONE("Done"),
    CANCELLED("Cancelled");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
