package org.example.coretrack.dto.productionTicket;

import java.util.List;

import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatus;

public class StatusTransitionRule {
    private ProductionTicketDetailStatus currentStatus;
    private List<ProductionTicketDetailStatus> allowedTransitions;
    private String description;

    public StatusTransitionRule(ProductionTicketDetailStatus currentStatus, 
                              List<ProductionTicketDetailStatus> allowedTransitions, 
                              String description) {
        this.currentStatus = currentStatus;
        this.allowedTransitions = allowedTransitions;
        this.description = description;
    }

    public StatusTransitionRule() {}

    public ProductionTicketDetailStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ProductionTicketDetailStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<ProductionTicketDetailStatus> getAllowedTransitions() {
        return allowedTransitions;
    }

    public void setAllowedTransitions(List<ProductionTicketDetailStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 