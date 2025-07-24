package org.example.coretrack.dto.supplier;

import org.example.coretrack.model.supplier.Supplier;

public class SearchSupplierResponse {
    private Long id;
    private String name;
    private String contactPerson ;
    private String email;
    private String phone;
    private String address;
    private String country;
    private String description;
    private Boolean isActive;

    public SearchSupplierResponse(Long id, String name, String contactPerson, String email, 
                                    String address, String country, String phone, String description, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.country = country;
        this.description = description;
        this.isActive = isActive;
    }

    public SearchSupplierResponse(){
    }

    // to map the supplier instance
    public SearchSupplierResponse(Supplier supplier){
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.email = supplier.getEmail();
        this.phone = supplier.getPhone();
        this.address = supplier.getFullAddress();
        this.country = supplier.getCountry();
        this.description = supplier.getDescription();
        this.isActive = supplier.getIsActive();
    }

    // getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
}
