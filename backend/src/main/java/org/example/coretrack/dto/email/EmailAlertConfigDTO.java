package org.example.coretrack.dto.email;

import java.util.List;

public class EmailAlertConfigDTO {
    private boolean lowStockEnabled;
    private boolean overStockEnabled;
    private boolean outOfStockEnabled;
    private boolean ticketStatusChangeEnabled;
    private List<String> recipientEmails;

    // Constructors
    public EmailAlertConfigDTO() {}

    public EmailAlertConfigDTO(boolean lowStockEnabled, boolean overStockEnabled, boolean outOfStockEnabled, 
                             boolean ticketStatusChangeEnabled, List<String> recipientEmails) {
        this.lowStockEnabled = lowStockEnabled;
        this.overStockEnabled = overStockEnabled;
        this.outOfStockEnabled = outOfStockEnabled;
        this.ticketStatusChangeEnabled = ticketStatusChangeEnabled;
        this.recipientEmails = recipientEmails;
    }

    // Getters and Setters
    public boolean isLowStockEnabled() {
        return lowStockEnabled;
    }

    public void setLowStockEnabled(boolean lowStockEnabled) {
        this.lowStockEnabled = lowStockEnabled;
    }

    public boolean isOverStockEnabled() {
        return overStockEnabled;
    }

    public void setOverStockEnabled(boolean overStockEnabled) {
        this.overStockEnabled = overStockEnabled;
    }

    public boolean isOutOfStockEnabled() {
        return outOfStockEnabled;
    }

    public void setOutOfStockEnabled(boolean outOfStockEnabled) {
        this.outOfStockEnabled = outOfStockEnabled;
    }

    public boolean isTicketStatusChangeEnabled() {
        return ticketStatusChangeEnabled;
    }

    public void setTicketStatusChangeEnabled(boolean ticketStatusChangeEnabled) {
        this.ticketStatusChangeEnabled = ticketStatusChangeEnabled;
    }

    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
    }
} 