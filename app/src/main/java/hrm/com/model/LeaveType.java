package hrm.com.model;

import java.io.Serializable;

/**
 * Created by Beans on 4/7/2015.
 */public class LeaveType implements Serializable {
    /**
     *
     */
    private int id;
    private String name;
    private String description;
    private double entitlement;
    private boolean isAccountable = false;
    private boolean isDeleted= false;
    private String createdBy;
    private java.util.Date creationTime;
    private String lastModifiedBy;
    private java.util.Date lastModifiedTime;
    private EmployeeType employeeType;



    public LeaveType() {
        super();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getEntitlement() {
        return entitlement;
    }
    public void setEntitlement(double entitlement) {
        this.entitlement = entitlement;
    }

    public boolean isAccountable() {
        return isAccountable;
    }
    public void setAccountable(boolean isAccountable) {
        this.isAccountable = isAccountable;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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

    public EmployeeType getEmployeeType() {
        return employeeType;
    }
    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }
    @Override
    public String toString() {
        return "LeaveType [id=" + id + ", name=" + name + ", description="
                + description + ", entitlement=" + entitlement
                + ", isAccountable=" + isAccountable + ", isDeleted="
                + isDeleted + ", createdBy=" + createdBy + ", creationTime="
                + creationTime + ", lastModifiedBy=" + lastModifiedBy
                + ", lastModifiedTime=" + lastModifiedTime + ", employeeType="
                + employeeType + "]";
    }



}

