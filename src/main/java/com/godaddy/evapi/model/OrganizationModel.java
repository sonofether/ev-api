package com.godaddy.evapi.model;

import java.util.UUID;

public class OrganizationModel extends BaseModel {

    private UUID id;
    private String organizationName;
    private String commonName;
    private String organizationalUnitName;
    private String serialNumber;
    private String localityName;
    private String stateOrProvinceName;
    private String countryName;
    
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
    
    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }
    
    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
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
    
}
