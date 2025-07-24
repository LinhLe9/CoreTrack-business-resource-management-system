package org.example.coretrack.dto.supplier;

import org.example.coretrack.model.supplier.Supplier;

public class AllSupplierSearchResponse {
    private Long id;
    private String name;
    private String contactPerson ;
    private String address;
    private String country;
    private Boolean isActive;

    public AllSupplierSearchResponse(Long id, String name, String contactPerson, String address, String country,
            Boolean isActive) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.address = address;
        this.country = country;
        this.isActive = isActive;
    }

    public AllSupplierSearchResponse(){
    }

    public AllSupplierSearchResponse(Supplier supplier){
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.address = supplier.getAddress() + ", " + supplier.getCity();
        this.country = supplier.getCountry();
        this.isActive = supplier.getIsActive();
    }
    
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}
