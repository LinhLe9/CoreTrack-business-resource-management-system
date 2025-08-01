package org.example.coretrack.dto.productionTicket;

import java.util.List;

public class BulkCreateProductionTicketResponse {
    private boolean success;
    private String message;
    private List<CreateProductionTicketResponse> createdTickets;
    private List<String> errors;
    private int totalRequested;
    private int totalCreated;
    private int totalFailed;

    public BulkCreateProductionTicketResponse() {}

    public BulkCreateProductionTicketResponse(boolean success, String message, 
                                           List<CreateProductionTicketResponse> createdTickets,
                                           List<String> errors, int totalRequested, 
                                           int totalCreated, int totalFailed) {
        this.success = success;
        this.message = message;
        this.createdTickets = createdTickets;
        this.errors = errors;
        this.totalRequested = totalRequested;
        this.totalCreated = totalCreated;
        this.totalFailed = totalFailed;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CreateProductionTicketResponse> getCreatedTickets() {
        return createdTickets;
    }

    public void setCreatedTickets(List<CreateProductionTicketResponse> createdTickets) {
        this.createdTickets = createdTickets;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getTotalRequested() {
        return totalRequested;
    }

    public void setTotalRequested(int totalRequested) {
        this.totalRequested = totalRequested;
    }

    public int getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(int totalCreated) {
        this.totalCreated = totalCreated;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(int totalFailed) {
        this.totalFailed = totalFailed;
    }
} 