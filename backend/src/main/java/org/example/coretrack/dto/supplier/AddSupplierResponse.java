package org.example.coretrack.dto.supplier;

import org.example.coretrack.model.supplier.Supplier;

public class AddSupplierResponse {
    private String name;
    private String contactPerson ;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String website;
    private String currency;

    public AddSupplierResponse(String name, String contactPerson, String email, String phone, String address,
            String city, String country, String website, String currency) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.website = website;
        this.currency = currency;
    }

    // to convert from supplier
    public AddSupplierResponse (Supplier supplier){
        this.address = supplier.getAddress();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.email = supplier.getEmail();
        this.phone = supplier.getPhone();
        this.address = supplier.getAddress();
        this.city = supplier.getCity();
        this.country = supplier.getCountry();
        this.website = supplier.getWebsite();
        this.currency = supplier.getCurrency();
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
