package org.example.coretrack.model.purchasingTicket;

public enum PurchasingTicketStatus {
    NEW("New"),
    PARTIAL_APPROVAL("Partial Aprroved"),
    APPROVAL("Approved"),
    PARTIAL_SUCCESSFUL("Partial succeed"),
    SUCCESSFUL("SUCCEED"),
    PARTIAL_SHIPPING ("Partial shipped"),
    SHIPPING("SHIPPED"),
    PARTIAL_READY("Partial ready"),
    READY("Ready"),
    CLOSED("Closed"),
    PARTIAL_CANCELLED ("Partial cancelled"),
    CANCELLED("Cancelled");

    private final String displayName;

    PurchasingTicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 