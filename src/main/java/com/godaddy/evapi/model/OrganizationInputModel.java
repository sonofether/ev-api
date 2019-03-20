package com.godaddy.evapi.model;

import java.util.Date;

public class OrganizationInputModel extends BaseModel {

    private String organizationName;
    private String commonName;
    private Date issuedDate;
    private Date expirationDate;
    private String serialNumber;
    private String localityName;
    private String stateOrProvinceName;
    private String countryName;
    private String phoneNumber;
    private String address;
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public String getCommonName() {
        return commonName;
    }
    
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    public Date getIssuedDate() {
        return issuedDate;
    }
    
    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getLocalityName() {
        return localityName;
    }
    
    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }
    
    public String getStateOrProvinceName() {
        return stateOrProvinceName;
    }
    
    public void setStateOrProvinceName(String stateOrProvinceName) {
        this.stateOrProvinceName = stateOrProvinceName;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }
    
}
