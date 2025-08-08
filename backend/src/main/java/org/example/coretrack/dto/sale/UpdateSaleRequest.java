package org.example.coretrack.dto.sale;

import jakarta.validation.constraints.Email;

public class UpdateSaleRequest {
    private String expected_complete_date;
    
    private String customerName;
    
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String customerPhone;
    
    private String customerAddress;

    public UpdateSaleRequest(){}

    public UpdateSaleRequest(String expected_complete_date, String customerName,
            @Email(message = "Invalid email format") String customerEmail, String customerPhone,
            String customerAddress) {
        this.expected_complete_date = expected_complete_date;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
    }

    public String getExpected_complete_date() {
        return expected_complete_date;
    }

    public void setExpected_complete_date(String expected_complete_date) {
        this.expected_complete_date = expected_complete_date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
    
}
