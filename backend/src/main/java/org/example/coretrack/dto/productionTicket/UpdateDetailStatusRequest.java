package org.example.coretrack.dto.productionTicket;

import org.example.coretrack.model.productionTicket.ProductionTicketDetailStatus;

public class UpdateDetailStatusRequest {
    private ProductionTicketDetailStatus newStatus;
    private String note;

    public UpdateDetailStatusRequest() {}

    public UpdateDetailStatusRequest(ProductionTicketDetailStatus newStatus, String note) {
        this.newStatus = newStatus;
        this.note = note;
    }

    public ProductionTicketDetailStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProductionTicketDetailStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
} 