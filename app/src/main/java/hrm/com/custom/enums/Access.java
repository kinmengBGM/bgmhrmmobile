package hrm.com.custom.enums;

/**
 * Created by Beans on 4/24/2015.
 */
public enum Access {
    HOME("Home", new String[]{"ROLE_USER", "ROLE_EMPLOYEE"}),
    PROFILE("My Profile", new String[]{"ROLE_USER", "ROLE_EMPLOYEE"}),
    APPLYLEAVE("Apply Leave", new String[]{"ROLE_EMPLOYEE", "ROLE_JRHR", "ROLE_HRSR", "ROLE_HR_MANAGER", "ROLE_TEAMLEAD", "ROLE_PM", "ROLE_PROJCOR"}),
    LEAVEHISTORY("Leave History", new String[]{"ROLE_EMPLOYEE", "ROLE_JRHR", "ROLE_HRSR", "ROLE_HR_MANAGER", "ROLE_TEAMLEAD", "ROLE_PM", "ROLE_PROJCOR"}),
    UPCOMINGLEAVE("Upcoming Leaves", new String[]{"ROLE_EMPLOYEE", "ROLE_JRHR", "ROLE_HRSR", "ROLE_HR_MANAGER", "ROLE_TEAMLEAD", "ROLE_PM", "ROLE_PROJCOR"}),
    LEAVEAPPROVAL("Leave Approval", new String[]{"ROLE_HR_MANAGER", "ROLE_TEAMLEAD", "ROLE_OPERDIR", "ROLE_MANGDIR", "ROLE_DIR"});

    private final String value;
    private final String[] roleList;


    private Access(String value, String[] roleList) {
        this.value = value;
        this.roleList = roleList;
    }

    public String toString(){
        return value;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:value.equalsIgnoreCase(otherName);
    }

    public boolean hasAccess(String role){
        for (String x:roleList){
            if(x.equals(role))
                return true;
        }
        return false;
    }
}
