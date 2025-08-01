package org.example.coretrack.model.productionTicket;

public enum ProductionTicketStatus {
    NEW("New"),
    IN_PROGRESS("In Progress"),
    PARTIAL_COMPLETE("Partial Complete"),
    COMPLETE("Complete"),
    CLOSED("Closed"),
    PARTIAL_CANCELLED ("Partial Cancelled"),
    CANCELLED("Cancelled");

    private final String displayName;

    ProductionTicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
