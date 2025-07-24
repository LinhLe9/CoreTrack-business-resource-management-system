package org.example.coretrack.dto.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddSupplierRequest {
    @NotBlank(message = "Supplier name cannot be empty")
    @Size(max = 255, message = "Supplier name cannot exceed 255 characters")
    private String name;

    private String contactPerson ;

    @Email(message = "Invalid Email")
    private String email;

    @Pattern(
        regexp = "^\\+?[0-9 .-]{7,15}$", 
        message = "Invalid phone number"
    ) 
    private String phone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;
    
    @Pattern(
        regexp = "^(https?://)?([\\w.-]+)+(:\\d+)?(/([\\w/_.]*)?)?$",
        message = "Invalid website URL"
    )
    private String website;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;
    

    public AddSupplierRequest(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
