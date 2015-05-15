package hrm.com.webservice;

import android.os.AsyncTask;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hrm.com.custom.enums.Leave;
import hrm.com.custom.listener.TaskListener;
import hrm.com.model.LeaveFlowDecisionsTaken;
import hrm.com.model.LeaveRuleBean;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.LeaveType;
import hrm.com.model.Role;
import hrm.com.model.Users;
import hrm.com.model.YearlyEntitlement;

/**
 * Created by Beans on 5/5/2015.
 */
public class LeaveApprovalManagement {

    private boolean isApproved;
    private LeaveTransaction selectedLeaveRequest;
    private LeaveTransaction leaveTransactionPersist;
    private int leaveTransactionId;
    private Users user;
    private boolean insertDeleted = false;

    private LeaveApplicationFlowWS leaveApplicationFlowWS;
    private LeaveTransactionWS leaveTransactionWS;
    private LeaveTypeWS leaveTypeWS;
    private YearlyEntitlementWS yearlyEntitlementWS;
    private LeaveApplicationEmailNotificationWS sendEmailWS;
    private CalendarEventWS calendarEventWS;

    public LeaveApprovalManagement(String username, String password, int leaveTransactionId, Users user) {
        this.leaveTransactionId = leaveTransactionId;
        this.user = user;

        leaveApplicationFlowWS = new LeaveApplicationFlowWS(username, password);
        leaveTransactionWS = new LeaveTransactionWS(username, password);
        leaveTypeWS = new LeaveTypeWS(username, password);
        yearlyEntitlementWS = new YearlyEntitlementWS(username, password);
        sendEmailWS = new LeaveApplicationEmailNotificationWS(username, password);
        calendarEventWS = new CalendarEventWS(username, password);
    }

    public void doRejectLeaveRequest(final String rejectReason, final TaskListener listener) {
        isApproved = false;

        doRejectTask rejectTask = new doRejectTask(rejectReason, new TaskListener() {
            @Override
            public void onTaskCompleted() {
                listener.onTaskCompleted();
            }

            @Override
            public void onTaskNotCompleted() {
                listener.onTaskNotCompleted();
            }
        });
        rejectTask.execute();
    }

    public void doApproveLeaveRequest(final TaskListener listener) {
        isApproved = true;
        doApproveTask approveTask = new doApproveTask(new TaskListener() {
            @Override
            public void onTaskCompleted() {
                listener.onTaskCompleted();
            }

            @Override
            public void onTaskNotCompleted() {
                listener.onTaskNotCompleted();
            }
        });
        approveTask.execute();
    }

    public Users getUser() {
        return user;
    }

    private Map<String, Integer> getMapByLeaveRules(LeaveRuleBean leaveRule) {
        Map<String, Integer> rulesMap = new HashMap<String, Integer>();
        if (StringUtils.trimToNull(leaveRule.getApproverNameLevel1()) != null)
            rulesMap.put(leaveRule.getApproverNameLevel1(), 1);
        if (StringUtils.trimToNull(leaveRule.getApproverNameLevel2()) != null)
            rulesMap.put(leaveRule.getApproverNameLevel2(), 2);
        if (StringUtils.trimToNull(leaveRule.getApproverNameLevel3()) != null)
            rulesMap.put(leaveRule.getApproverNameLevel3(), 3);
        if (StringUtils.trimToNull(leaveRule.getApproverNameLevel4()) != null)
            rulesMap.put(leaveRule.getApproverNameLevel4(), 4);
        if (StringUtils.trimToNull(leaveRule.getApproverNameLevel5()) != null)
            rulesMap.put(leaveRule.getApproverNameLevel5(), 5);
        return rulesMap;
    }

    private class doRejectTask extends AsyncTask<String, Void, Boolean> {

        String rejectReason;
        TaskListener taskListener;

        public doRejectTask(String rejectReason, TaskListener taskListener) {
            this.rejectReason = rejectReason;
            this.taskListener = taskListener;
        }

        @Override
        protected void onPostExecute(Boolean results) {
            super.onPostExecute(results);
            if (results)
                taskListener.onTaskCompleted();
            else
                taskListener.onTaskNotCompleted();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                selectedLeaveRequest = leaveTransactionWS.findById(leaveTransactionId);
                LeaveRuleBean leaveRule = selectedLeaveRequest.getLeaveRuleBean();
                leaveTransactionPersist = null;
                Map<String, Integer> rulesMap = getMapByLeaveRules(leaveRule);

                if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 1) {
                    selectedLeaveRequest.getDecisionsBean().setDecisionLevel1("NO");
                    selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel1(getUser().getUsername());
                } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 2) {
                    selectedLeaveRequest.getDecisionsBean().setDecisionLevel2("NO");
                    selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel2(getUser().getUsername());
                } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 3) {
                    selectedLeaveRequest.getDecisionsBean().setDecisionLevel3("NO");
                    selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel3(getUser().getUsername());
                } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 4) {
                    selectedLeaveRequest.getDecisionsBean().setDecisionLevel4("NO");
                    selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel4(getUser().getUsername());
                } else {
                    selectedLeaveRequest.getDecisionsBean().setDecisionLevel5("NO");
                    selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel5(getUser().getUsername());
                }
                selectedLeaveRequest.setRejectReason(rejectReason);
                // saving the decisions taken by the approvers on a list
                LeaveFlowDecisionsTaken leaveFlowDecisions = leaveTransactionWS.saveLeaveApprovalDecisions(selectedLeaveRequest.getDecisionsBean());
                selectedLeaveRequest.setDecisionsBean(leaveFlowDecisions);
                selectedLeaveRequest.setLastModifiedBy(getUser().getUsername());

                leaveTransactionPersist = leaveTransactionWS.processAppliedLeaveOfEmployee(selectedLeaveRequest);
                leaveApplicationFlowWS.UpdateLeaveBalancesOnceApprovedTask(isApproved, leaveTransactionPersist);
                sendEmailWS.sendingIntimationEmail(leaveTransactionPersist);
            }catch(Exception e) {
                return false;
            }
            return true;
        }
    }

    private class doApproveTask extends AsyncTask<String, Void, Boolean> {

        TaskListener taskListener;

        public doApproveTask(TaskListener taskListener) {
            this.taskListener = taskListener;
        }

        @Override
        protected void onPostExecute(Boolean results){
            super.onPostExecute(results);
            if (results)
                taskListener.onTaskCompleted();
            else
                taskListener.onTaskNotCompleted();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                selectedLeaveRequest = leaveTransactionWS.findById(leaveTransactionId);
                LeaveRuleBean leaveRule = selectedLeaveRequest.getLeaveRuleBean();
                leaveTransactionPersist = null;

                Map<String, Integer> rulesMap = getMapByLeaveRules(leaveRule);
                int totalLevelsRequired = rulesMap.size();

                Integer currentLevel = rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken());
                if (currentLevel == totalLevelsRequired) {
                    if (totalLevelsRequired == 1) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel1("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel1(getUser().getUsername());
                    } else if (totalLevelsRequired == 2) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel2("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel2(getUser().getUsername());
                    } else if (totalLevelsRequired == 3) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel3("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel3(getUser().getUsername());
                    } else if (totalLevelsRequired == 4) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel4("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel4(getUser().getUsername());
                    } else {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel5("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel5(getUser().getUsername());
                    }
                    selectedLeaveRequest.setDecisionToBeTaken("NONE");
                    // saving the decisions taken by the approvers on a list
                    LeaveFlowDecisionsTaken leaveFlowDecisions = leaveTransactionWS.saveLeaveApprovalDecisions(selectedLeaveRequest.getDecisionsBean());
                    selectedLeaveRequest.setDecisionsBean(leaveFlowDecisions);
                    selectedLeaveRequest.setLastModifiedBy(getUser().getUsername());

                    // Getting the latest yearly balance and do the operations on it.
                    YearlyEntitlement entitlementBean = null;
                    if (Leave.TIMEINLIEU.equalsName(selectedLeaveRequest.getLeaveType().getName())) {
                        LeaveType leaveType = leaveTypeWS.findByEmployeeNameAndTypeId(Leave.ANNUAL.toString(), selectedLeaveRequest.getEmployee().getEmployeeType().getId());
                        entitlementBean = yearlyEntitlementWS.findByEmployeeAndLeaveType(selectedLeaveRequest.getEmployee().getId(), leaveType.getId());
                    } else
                        entitlementBean = yearlyEntitlementWS.findByEmployeeAndLeaveType(selectedLeaveRequest.getEmployee().getId(), selectedLeaveRequest.getLeaveType().getId());

                    if (entitlementBean != null)
                        selectedLeaveRequest.setYearlyLeaveBalance(entitlementBean.getYearlyLeaveBalance());

                    leaveTransactionPersist = leaveTransactionWS.processAppliedLeaveOfEmployee(selectedLeaveRequest);
                    leaveApplicationFlowWS.UpdateLeaveBalancesOnceApprovedTask(isApproved, leaveTransactionPersist);

                    calendarEventWS.createEventForApprovedLeave(selectedLeaveRequest);

                } else {
                    if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 1) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel1("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel1(getUser().getUsername());
                        selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel2());
                    } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 2) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel2("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel2(getUser().getUsername());
                        selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel3());
                    } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 3) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel3("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel3(getUser().getUsername());
                        selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel3());
                    } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 4) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel4("YES");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel4(getUser().getUsername());
                        selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel4());
                    }
                    // saving the decisions taken by the approvers on a list
                    LeaveFlowDecisionsTaken leaveFlowDecisions = leaveTransactionWS.saveLeaveApprovalDecisions(selectedLeaveRequest.getDecisionsBean());
                    selectedLeaveRequest.setDecisionsBean(leaveFlowDecisions);
                    selectedLeaveRequest.setLastModifiedBy(getUser().getUsername());
                    leaveTransactionPersist = leaveTransactionWS.processAppliedLeaveOfEmployee(selectedLeaveRequest);

                }
                sendEmailWS.sendingIntimationEmail(leaveTransactionPersist);

                Set<String> roleSet = new HashSet<String>();
                for (Role role : getUser().getUserRoles()) {
                    roleSet.add(role.getRole());
                }
            }catch(Exception e) {
                return false;
            }

            setInsertDeleted(true);
            return true;
        }
    }

    public void setInsertDeleted(boolean insertDeleted) {
        this.insertDeleted = insertDeleted;
    }

}
