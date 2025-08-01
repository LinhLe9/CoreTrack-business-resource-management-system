package org.example.coretrack.model.productionTicket;

public enum ProductionTicketDetailStatus {
    NEW("New"),
    APPROVAL ("Approved"),
    COMPLETE("Complete"),
    READY("Ready"),
    CLOSED("Closed"),
    CANCELLED("Cancelled");

    private final String displayName;

    ProductionTicketDetailStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
