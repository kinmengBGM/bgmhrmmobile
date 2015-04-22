package hrm.com.model;

import java.io.Serializable;

/**
 * Created by Beans on 3/31/2015.
 */
public class Address implements Serializable {
    private int id;
    private Employee employee;
    private String addressType;
    private String line1;
    private String line2;
    private String line3;
    private String city;
    private String state;
    private String country;
    private String postcode;
    private String createdBy;
    private java.util.Date creationTime;
    private String lastModifiedBy;
    private java.util.Date lastModifiedTime;

    private boolean isDeleted= false;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public String getAddressType() {
        return addressType;
    }
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    public String getLine1() {
        return line1;
    }
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }
    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public void setCreationTime(java.util.Date creationTime) {
        this.creationTime = creationTime;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setLastModifiedTime(java.util.Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public java.util.Date getCreationTime() {
        return creationTime;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public java.util.Date getLastModifiedTime() {
        return lastModifiedTime;
    }
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
