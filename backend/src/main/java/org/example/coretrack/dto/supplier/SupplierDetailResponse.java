package org.example.coretrack.dto.supplier;

import java.util.List;

import org.example.coretrack.model.supplier.Supplier;

public class SupplierDetailResponse {
    private Long id;
    private String name;
    private String contactPerson ;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String website;
    private String currency; 
    private String description;
    private Boolean isActive;
    private List <SupplierMaterialResponse> supplierMaterial;

    public SupplierDetailResponse(Long id, String name, String contactPerson, String email, String phone,
            String address, String city, String country, String website, String currency, String description,
            Boolean isActive, List<SupplierMaterialResponse> supplierMaterial) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.website = website;
        this.currency = currency;
        this.description = description;
        this.isActive = isActive;
        this.supplierMaterial = supplierMaterial;
    }

    public SupplierDetailResponse(){
    }

    public SupplierDetailResponse(Supplier supplier, List<SupplierMaterialResponse> listMaterial){
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.email = supplier.getEmail();
        this.phone = supplier.getPhone();
        this.address = supplier.getAddress();
        this.city = supplier.getCity();
        this.country = supplier.getCountry();
        this.website = supplier.getWebsite();
        this.currency = supplier.getCurrency();
        this.description = supplier.getDescription();
        this.isActive = supplier.getIsActive();
        this.supplierMaterial = listMaterial;
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

    public List<SupplierMaterialResponse> getSupplierMaterial() {
        return supplierMaterial;
    }

    public void setSupplierMaterial(List<SupplierMaterialResponse> supplierMaterial) {
        this.supplierMaterial = supplierMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
}
