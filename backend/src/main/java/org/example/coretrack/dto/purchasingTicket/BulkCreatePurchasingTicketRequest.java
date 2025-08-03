package org.example.coretrack.dto.purchasingTicket;

import java.util.List;

public class BulkCreatePurchasingTicketRequest {
    private String name;
    List<CreatePurchasingTicketRequest> singleTicket;
    
    public BulkCreatePurchasingTicketRequest(String name, List<CreatePurchasingTicketRequest> singleTicket) {
        this.name = name;
        this.singleTicket = singleTicket;
    }

    public BulkCreatePurchasingTicketRequest(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CreatePurchasingTicketRequest> getSingleTicket() {
        return singleTicket;
    }

    public void setSingleTicket(List<CreatePurchasingTicketRequest> singleTicket) {
        this.singleTicket = singleTicket;
    }
    
}
