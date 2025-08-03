package org.example.coretrack.dto.sale;

import org.example.coretrack.model.Sale.OrderStatus;

public class UpdateSaleOrderStatusRequest {
    private OrderStatus newStatus;
    private String note;

    public UpdateSaleOrderStatusRequest(OrderStatus newStatus, String note) {
        this.newStatus = newStatus;
        this.note = note;
    }
    
    public UpdateSaleOrderStatusRequest(){}

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    
}

