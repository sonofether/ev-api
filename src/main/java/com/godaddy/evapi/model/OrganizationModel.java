package com.godaddy.evapi.model;

import java.util.UUID;

public class OrganizationModel extends BaseModel {
    private UUID id;
    private String organizationName;
    private String commonName;
    private String serialNumber;
    private String localityName;
    private String stateOrProvinceName;
    private String countryName;
    private String countryCode;
    private String ca;
    private String phoneNumber;
    private String address;
    
    public OrganizationModel() {
        // Default constructor - do nothing
    }
        
    public OrganizationModel(UUID id, String orgName, String cName, String serialNum, String locality, String state, String country, String ca,
                String phoneNumber, String address) {
        this.id = id;
        this.setValues(orgName, cName, serialNum, locality, state, country, ca, phoneNumber, address);
    }
    
    public OrganizationModel( String orgName, String cName, String serialNum, String locality, String state, String country, String ca,
                String phoneNumber, String address) {
        this.id = UUID.randomUUID();
        this.setValues(orgName, cName, serialNum, locality, state, country, ca, phoneNumber, address);
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
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
    
    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }
    
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    // PRIVATE calls
    private void setValues(String orgName, String cName, String serialNum, String locality, String state, String country, String ca,
                String phoneNumber, String address) {
        this.organizationName = orgName;
        this.commonName = cName;
        this.serialNumber = serialNum;
        this.localityName = locality;
        this.stateOrProvinceName = state;
        this.countryName = country;
        this.ca = ca;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
}
