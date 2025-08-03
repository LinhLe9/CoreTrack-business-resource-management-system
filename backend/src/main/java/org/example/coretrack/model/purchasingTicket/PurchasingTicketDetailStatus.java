package org.example.coretrack.model.purchasingTicket;

public enum PurchasingTicketDetailStatus {
    NEW("New"),
    APPROVAL ("Approved"),
    SUCCESSFUL("SUCCEED"),
    SHIPPING("SHIPPED"),
    READY("Ready"),
    CLOSED("Closed"),
    CANCELLED("Cancelled");

    private final String displayName;

    PurchasingTicketDetailStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
