package hrm.com.model;

/**
 * Created by Beans on 4/7/2015.
 */

import java.io.Serializable;

public class YearlyEntitlement implements Serializable {

    /**
     *
     */
    private int id;;
    private double entitlement;
    private double currentLeaveBalance;
    private double yearlyLeaveBalance;
    private boolean isDeleted = false;
    private String createdBy;
    private java.util.Date creationTime;
    private String lastModifiedBy;
    private java.util.Date lastModifiedTime;
    private Employee employee;
    private LeaveType leaveType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getEntitlement() {
        return entitlement;
    }

    public void setEntitlement(double entitlement) {
        this.entitlement = entitlement;
    }

    public double getCurrentLeaveBalance() {
        return currentLeaveBalance;
    }

    public void setcurrentLeaveBalance(double currentLeaveBalance) {
        this.currentLeaveBalance = currentLeaveBalance;
    }

    public double getYearlyLeaveBalance() {
        return yearlyLeaveBalance;
    }

    public void setYearlyLeaveBalance(double yearlyLeaveBalance) {
        this.yearlyLeaveBalance = yearlyLeaveBalance;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;


    }

    @Override
    public String toString() {
        return getLeaveType().getDescription();
    }
}

