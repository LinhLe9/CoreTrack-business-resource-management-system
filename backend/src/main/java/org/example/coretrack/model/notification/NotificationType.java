package org.example.coretrack.model.notification;

public enum NotificationType {
    INVENTORY_OUT_OF_STOCK("Out of Stock", "Product is out of stock"),
    INVENTORY_LOW_STOCK("Low Stock", "Product stock is below minimum level"),
    INVENTORY_OVER_STOCK("Over Stock", "Product stock exceeds maximum level"),
    INVENTORY_STATUS_CHANGE("Status Change", "Product inventory status has changed"),
    PRODUCTION_TICKET_STATUS_CHANGE("Production Ticket Status", "Production ticket status has changed"),
    PRODUCTION_TICKET_DETAIL_STATUS_CHANGE("Production Detail Status", "Production ticket detail status has changed"),
    PURCHASING_TICKET_STATUS_CHANGE("Purchasing Ticket Status", "Purchasing ticket status has changed"),
    PURCHASING_TICKET_DETAIL_STATUS_CHANGE("Purchasing Detail Status", "Purchasing ticket detail status has changed"),
    SALE_STATUS_CHANGE("Sale Status", "Sale order status has changed"),
    SALE_CANCELLED("Sale Cancelled", "Sale order has been cancelled");
    
    private final String displayName;
    private final String description;
    
    NotificationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
} 