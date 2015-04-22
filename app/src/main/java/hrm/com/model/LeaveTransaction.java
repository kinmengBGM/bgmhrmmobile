package hrm.com.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LeaveTransaction implements Serializable {

    /**
     *
     */
    private int id;
    private Date applicationDate;
    private Date startDateTime;
    private Date endDateTime;
    private Double yearlyLeaveBalance;
    private Double numberOfDays;
    private String reason;
    private LeaveType leaveType;
    private Employee employee;
    private Long taskId;
    private String status;
    private String rejectReason;
    private String timings;
    private String sickLeaveAttachmentName;
    private LeaveRuleBean leaveRuleBean;
    private LeaveFlowDecisionsTaken decisionsBean;
    private String decisionToBeTaken;

    private byte[] sickLeaveAttachment;
    private String createdBy;
    private Date creationTime;
    private String lastModifiedBy;
    private Date lastModifiedTime;
    private boolean isDelete;

    public LeaveTransaction() {

    }


    public LeaveTransaction(int id, Date applicationDate, Date startDateTime,
                            Date endDateTime, Double yearlyLeaveBalance, Double numberOfDays,
                            String reason, LeaveType leaveType, Employee employee,
                            String status, String timings, LeaveRuleBean leaveRuleBean,
                            LeaveFlowDecisionsTaken decisionsBean, boolean isDelete) {
        super();
        this.id = id;
        this.applicationDate = applicationDate;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.yearlyLeaveBalance = yearlyLeaveBalance;
        this.numberOfDays = numberOfDays;
        this.reason = reason;
        this.leaveType = leaveType;
        this.employee = employee;
        this.status = status;
        this.timings = timings;
        this.leaveRuleBean = leaveRuleBean;
        this.decisionsBean = decisionsBean;
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {

        this.applicationDate = applicationDate;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String fetchStartTimeStr() {
        if (startDateTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(startDateTime);
        }
        return startDateTime.toString();
    }

    public String fetchEndTimeStr() {
        if (endDateTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(endDateTime);
        }
        return endDateTime.toString();
    }


    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Double getYearlyLeaveBalance() {
        return yearlyLeaveBalance;
    }

    public void setYearlyLeaveBalance(Double yearlyLeaveBalance) {
        this.yearlyLeaveBalance = yearlyLeaveBalance;
    }

    public Double getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Double numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDeleted() {
        return isDelete;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDelete = isDeleted;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public byte[] getSickLeaveAttachment() {
        return sickLeaveAttachment;
    }

    public void setSickLeaveAttachment(byte[] sickLeaveAttachment) {
        this.sickLeaveAttachment = sickLeaveAttachment;
    }

    public String getDecisionToBeTaken() {
        return decisionToBeTaken;
    }

    public void setDecisionToBeTaken(String decisionToBeTaken) {
        this.decisionToBeTaken = decisionToBeTaken;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;

    }

    public String getSickLeaveAttachmentName() {
        return sickLeaveAttachmentName;
    }

    public void setSickLeaveAttachmentName(String sickLeaveAttachmentName) {
        this.sickLeaveAttachmentName = sickLeaveAttachmentName;
    }

    public LeaveRuleBean getLeaveRuleBean() {
        return leaveRuleBean;
    }

    public void setLeaveRuleBean(LeaveRuleBean leaveRuleBean) {
        this.leaveRuleBean = leaveRuleBean;
    }

    public LeaveFlowDecisionsTaken getDecisionsBean() {
        return decisionsBean;
    }

    public void setDecisionsBean(LeaveFlowDecisionsTaken decisionsBean) {
        this.decisionsBean = decisionsBean;
    }

    @Override
    public String toString() {
        return "LeaveTransaction [id=" + id + ", status=" + status
                + ", leaveRuleBean=" + leaveRuleBean + ", decisionsBean="
                + decisionsBean + ", decisionToBeTaken=" + decisionToBeTaken
                + "]";
    }
}
