package org.example.coretrack.dto.productionTicket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.coretrack.model.auth.User;

public class CreateProductionTicketRequest {
    private String name;
    private String variantSku;
    private BigDecimal quantity;

    //bomItem 
    private List<BomItemProductionTicketRequest> boms;

    // date 
    private LocalDateTime expected_complete_date;
    private LocalDateTime complete_date;

    //Audit & status
    private User created_by;
    private LocalDateTime created_at;

    // constructor
    public CreateProductionTicketRequest(){}

    public CreateProductionTicketRequest(String name, String variantSku, BigDecimal quantity,
            List<BomItemProductionTicketRequest> boms, 
            LocalDateTime expected_complete_date,
            LocalDateTime complete_date, User created_by,
            LocalDateTime created_at) {
        this.name = name;
        this.variantSku = variantSku;
        this.quantity = quantity;
        this.boms = boms;
        this.expected_complete_date = expected_complete_date;
        this.complete_date = complete_date;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    //getter & setter
    public String getVariantSku() {
        return variantSku;
    }
    public void setVariantSku(String variantSku) {
        this.variantSku = variantSku;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public LocalDateTime getExpected_complete_date() {
        return expected_complete_date;
    }
    public void setExpected_complete_date(LocalDateTime expected_complete_date) {
        this.expected_complete_date = expected_complete_date;
    }
    public LocalDateTime getComplete_date() {
        return complete_date;
    }
    public void setComplete_date(LocalDateTime complete_date) {
        this.complete_date = complete_date;
    }

    public User getCreated_by() {
        return created_by;
    }
    public void setCreated_by(User created_by) {
        this.created_by = created_by;
    }
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BomItemProductionTicketRequest> getBoms() {
        return boms;
    }

    public void setBoms(List<BomItemProductionTicketRequest> boms) {
        this.boms = boms;
    }
}
