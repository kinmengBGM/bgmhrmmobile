package hrm.com.model;

import java.io.Serializable;

/**
 * Created by Beans on 4/7/2015.
 */
public class LeaveRuleBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private String leaveType;
    private String roleType;
    private String approverNameLevel1;
    private String approverNameLevel2;
    private String approverNameLevel3;
    private String approverNameLevel4;
    private String approverNameLevel5;
    private Boolean isDeleted;


    public void setId(int id) {
        this.id = id;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public void setApproverNameLevel1(String approverNameLevel1) {
        this.approverNameLevel1 = approverNameLevel1;
    }

    public void setApproverNameLevel2(String approverNameLevel2) {
        this.approverNameLevel2 = approverNameLevel2;
    }

    public void setApproverNameLevel3(String approverNameLevel3) {
        this.approverNameLevel3 = approverNameLevel3;
    }

    public void setApproverNameLevel4(String approverNameLevel4) {
        this.approverNameLevel4 = approverNameLevel4;
    }

    public void setApproverNameLevel5(String approverNameLevel5) {
        this.approverNameLevel5 = approverNameLevel5;
    }

    public int getId() {
        return id;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getRoleType() {
        return roleType;
    }

    public String getApproverNameLevel1() {
        return approverNameLevel1;
    }

    public String getApproverNameLevel2() {
        return approverNameLevel2;
    }

    public String getApproverNameLevel3() {
        return approverNameLevel3;
    }

    public String getApproverNameLevel4() {
        return approverNameLevel4;
    }

    public String getApproverNameLevel5() {
        return approverNameLevel5;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "LeaveRuleBean [id=" + id + ", leaveType=" + leaveType
                + ", roleType=" + roleType + ", approverNameLevel1="
                + approverNameLevel1 + ", approverNameLevel2="
                + approverNameLevel2 + ", approverNameLevel3="
                + approverNameLevel3 + ", approverNameLevel4="
                + approverNameLevel4 + ", approverNameLevel5="
                + approverNameLevel5 + "]";
    }

    public LeaveRuleBean() {
        super();
        // TODO Auto-generated constructor stub
    }
}