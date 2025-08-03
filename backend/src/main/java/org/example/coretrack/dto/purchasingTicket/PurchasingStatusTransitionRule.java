package org.example.coretrack.dto.purchasingTicket;

import java.util.List;

import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetailStatus;

public class PurchasingStatusTransitionRule {
    private PurchasingTicketDetailStatus currentStatus;
    private List<PurchasingTicketDetailStatus> allowedTransitions;
    private String description;
    
    public PurchasingStatusTransitionRule(PurchasingTicketDetailStatus currentStatus,
            List<PurchasingTicketDetailStatus> allowedTransitions, String description) {
        this.currentStatus = currentStatus;
        this.allowedTransitions = allowedTransitions;
        this.description = description;
    }

    public PurchasingStatusTransitionRule(){}

    public PurchasingTicketDetailStatus getCurrentStatus() {
        return currentStatus;
    }
    public void setCurrentStatus(PurchasingTicketDetailStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
    public List<PurchasingTicketDetailStatus> getAllowedTransitions() {
        return allowedTransitions;
    }
    public void setAllowedTransitions(List<PurchasingTicketDetailStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
