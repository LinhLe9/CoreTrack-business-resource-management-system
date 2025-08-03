package org.example.coretrack.dto.purchasingTicket;

import org.example.coretrack.model.purchasingTicket.PurchasingTicketDetailStatus;

public class UpdatePurchasingDetailStatusRequest {
    private PurchasingTicketDetailStatus newStatus;
    private String note;

    // constructor
    public UpdatePurchasingDetailStatusRequest(PurchasingTicketDetailStatus newStatus, String note) {
        this.newStatus = newStatus;
        this.note = note;
    }

    public UpdatePurchasingDetailStatusRequest(){}

    // getter setter
    public PurchasingTicketDetailStatus getNewStatus() {
        return newStatus;
    }
    public void setNewStatus(PurchasingTicketDetailStatus newStatus) {
        this.newStatus = newStatus;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
