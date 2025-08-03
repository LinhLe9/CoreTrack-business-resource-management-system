package org.example.coretrack.dto.sale;

import java.util.List;

import org.example.coretrack.model.Sale.OrderStatus;

public class SaleStatusTransitionRule {
    private OrderStatus currentStatus;
    private List<OrderStatus> allowedTransitions;
    private String description;
    
    public SaleStatusTransitionRule() {
    }
    public SaleStatusTransitionRule(OrderStatus currentStatus, List<OrderStatus> allowedTransitions,
            String description) {
        this.currentStatus = currentStatus;
        this.allowedTransitions = allowedTransitions;
        this.description = description;
    }
    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }
    public void setCurrentStatus(OrderStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
    public List<OrderStatus> getAllowedTransitions() {
        return allowedTransitions;
    }
    public void setAllowedTransitions(List<OrderStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    
}
