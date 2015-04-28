package hrm.com.leave;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hrm.com.custom.adapter.ApproveLeaveAdapter;
import hrm.com.custom.enums.Leave;
import hrm.com.custom.fragment.RejectLeaveDialog;
import hrm.com.custom.fragment.SickLeaveDialog;
import hrm.com.custom.listener.ApproveLeaveListener;
import hrm.com.custom.listener.RejectLeaveListener;
import hrm.com.custom.listener.TaskListener;
import hrm.com.custom.listener.ViewSickLeaveAttachmentListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveFlowDecisionsTaken;
import hrm.com.model.LeaveRuleBean;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.LeaveType;
import hrm.com.model.Role;
import hrm.com.model.Users;
import hrm.com.model.YearlyEntitlement;

/**
 * Created by Beans on 4/14/2015.
 */

@SuppressLint("ValidFragment")
public class ApproveLeaveTaskList extends Fragment {
    private ApproveLeaveAdapter adpt;
    private List<LeaveTransaction> approveLeaveList = new ArrayList<LeaveTransaction>();

    private TextView noLeaveHistory;

    private String username;
    private String password;
    private Users user;

    private String empUsername;

    private ListView lView;

    int leaveTransactionId;
    private boolean insertDeleted = false;
    private boolean leaveProcessCompleted = false;
    private LeaveTransaction selectedLeaveRequest;
    private LeaveTransaction leaveTransactionPersist;
    private String rejectReason;
    private boolean isApproved;
    private YearlyEntitlement entitlementBean = null;
    private RejectLeaveDialog rej;

    public ApproveLeaveTaskList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_approve_leave_list, container, false);

        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.user = ((HomeActivity) getActivity()).getActiveUser();
        empUsername = user.getUsername();

        lView = (ListView) rootView.findViewById(R.id.listViewAddress);
        noLeaveHistory = (TextView) rootView.findViewById(R.id.textView);

        PopulateApproveLeaveTaskList populate = new PopulateApproveLeaveTaskList();
        populate.execute();

        return rootView;
    }

    public void setApproveLeaveList(List<LeaveTransaction> leaveHistoryList) {
        this.approveLeaveList = leaveHistoryList;
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

    public void doApproveLeaveRequest() {
        try {
            //==VALUE FROM selectedEvent
            isApproved = Boolean.TRUE;
            GetLeaveTransactionTask getLeaveTransactionTask = new GetLeaveTransactionTask(new TaskListener() {

                @Override
                public void onTaskCompleted() {
                    LeaveRuleBean leaveRule = selectedLeaveRequest.getLeaveRuleBean();
                    leaveTransactionPersist = null;

                    Map<String, Integer> rulesMap = getMapByLeaveRules(leaveRule);
                    int totalLevelsRequired = rulesMap.size();

                    Integer currentLevel = rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken());
                    if (currentLevel == totalLevelsRequired) {
                        leaveProcessCompleted = true;
                        // write the code for leave process completed, do the operations like db updates, calendar events and other stuff
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
                        //==INJECT CONTROLLER===
                        SaveLeaveApprovalDecisionsTask saveLeaveApprovalDecisionsTask = new SaveLeaveApprovalDecisionsTask(selectedLeaveRequest.getDecisionsBean());
                        saveLeaveApprovalDecisionsTask.execute();


                    } else {
                        leaveProcessCompleted = false;
                        if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 1) {
                            selectedLeaveRequest.getDecisionsBean().setDecisionLevel1("YES");
                            selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel1(getUser().getUsername());
                            selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel2());
                            //ApplLogger.getLogger().info(String.format("Current Level is approved by : %s and case moved to next Level : %s", getActorUsers().getUsername(), leaveRule.getApproverNameLevel2()));
                        } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 2) {
                            selectedLeaveRequest.getDecisionsBean().setDecisionLevel2("YES");
                            selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel2(getUser().getUsername());
                            selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel3());
                            //ApplLogger.getLogger().info(String.format("Current Level is approved by : %s and case moved to next Level : %s", getActorUsers().getUsername(), leaveRule.getApproverNameLevel3()));
                        } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 3) {
                            selectedLeaveRequest.getDecisionsBean().setDecisionLevel3("YES");
                            selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel3(getUser().getUsername());
                            selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel3());
                            //ApplLogger.getLogger().info(String.format("Current Level is approved by : %s and case moved to next Level : %s", getActorUsers().getUsername(), leaveRule.getApproverNameLevel4()));
                        } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 4) {
                            selectedLeaveRequest.getDecisionsBean().setDecisionLevel4("YES");
                            selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel4(getUser().getUsername());
                            selectedLeaveRequest.setDecisionToBeTaken(leaveRule.getApproverNameLevel4());
                            //ApplLogger.getLogger().info(String.format("Current Level is approved by : %s and case moved to next Level : %s", getActorUsers().getUsername(), leaveRule.getApproverNameLevel5()));
                        }
                        // saving the decisions taken by the approvers on a list
                        SaveLeaveApprovalDecisionsTask saveLeaveApprovalDecisionsTask = new SaveLeaveApprovalDecisionsTask(selectedLeaveRequest.getDecisionsBean());
                        saveLeaveApprovalDecisionsTask.execute();
                        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Info : Leave is approved and case is moved to next level approval","Leave Approved"));
                    }
                    // sending email to notify
                    //LeaveApplicationEmailNotificationService.sendingIntimationEmail(leaveTransactionPersist);

                    Set<String> roleSet = new HashSet<String>();
                    for (Role role : getUser().getUserRoles()) {
                        roleSet.add(role.getRole());
                    }

                    setInsertDeleted(true);
                }
            });
            getLeaveTransactionTask.execute();
       /* } catch (BSLException e) {
            //FacesMessage msg = new FacesMessage("Error : "+getExcptnMesProperty(e.getMessage()),"Leave approve error");
            //msg.setSeverity(FacesMessage.SEVERITY_INFO);
            //FacesContext.getCurrentInstance().addMessage(null, msg);*/
        } catch (Exception e) {
            //log.error("Error while approving leave by " + getActorUsers().getUsername(), e);
            //FacesMessage msg = new FacesMessage("Error : "+getExcptnMesProperty(e.getMessage()),"Leave approve error");
            //msg.setSeverity(FacesMessage.SEVERITY_INFO);
            //FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void doRejectLeaveRequest() {
        try {
            isApproved = Boolean.FALSE;
            GetLeaveTransactionTask getLeaveTransactionTask = new GetLeaveTransactionTask(new TaskListener() {
                @Override
                public void onTaskCompleted() {
                    LeaveRuleBean leaveRule = selectedLeaveRequest.getLeaveRuleBean();
                    leaveTransactionPersist = null;
                    //ApplLogger.getLogger().info(String.format("Leave Application is rejected by user %s with role : %s", getActorUsers().getUsername(),selectedLeaveRequest.getDecisionToBeTaken()));
                    Map<String, Integer> rulesMap = getMapByLeaveRules(leaveRule);

                    if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 1) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel1("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel1(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s ", getActorUsers().getUsername()));
                    } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 2) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel2("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel2(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 3) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel3("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel3(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    } else if (rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken()) == 4) {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel4("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel4(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    } else {
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel5("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel5(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    }
                    selectedLeaveRequest.setRejectReason(rejectReason);
                    // saving the decisions taken by the approvers on a list
                    SaveLeaveApprovalDecisionsTask saveLeaveApprovalDecisions = new SaveLeaveApprovalDecisionsTask(selectedLeaveRequest.getDecisionsBean());
                    saveLeaveApprovalDecisions.execute();
                }
            });
            getLeaveTransactionTask.execute();


            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Info : "+getExcptnMesProperty("info.leave.reject"),"Leave Rejected"));
            //}catch(BSLException e){
            /*FacesMessage msg = new FacesMessage("Error : "+getExcptnMesProperty(e.getMessage()),"Leave Reject Error");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);*/
        } catch (Exception e) {/*
            log.error("Error while approving leave by "+getActorUsers().getUsername(), e);
            FacesMessage msg = new FacesMessage("Error : "+getExcptnMesProperty(e.getMessage()),"Leave approve error");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);*/
        }
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public void setApproveLeaveList() {

        adpt = new ApproveLeaveAdapter(getActivity().getApplicationContext(), R.layout.approve_leave_row_layout, approveLeaveList, new ApproveLeaveListener() {

            @Override
            public void onRejectSelected(int leaveId) {
                leaveTransactionId = leaveId;
                rej = new RejectLeaveDialog(new RejectLeaveListener() {
                    @Override
                    public void onRejectLeave(String reason) {
                        setRejectReason(reason);
                        doRejectLeaveRequest();
                    }
                });
                rej.show(getFragmentManager(), "Reject Leave");
            }

            @Override
            public void onApproveSelected(int leaveId) {
                leaveTransactionId = leaveId;
                Toast.makeText(getActivity().getApplicationContext(), "APPROVE " +leaveTransactionId, Toast.LENGTH_SHORT).show();
                doApproveLeaveRequest();
            }
        }, new ViewSickLeaveAttachmentListener() {
            @Override
            public void onViewAttachment(LeaveTransaction attachment){
                //TODO: Open pdf viewer
                SickLeaveDialog sickLeaveDialog = new SickLeaveDialog(attachment.getSickLeaveAttachment());
                sickLeaveDialog.show(getActivity().getSupportFragmentManager(), "AttachmentDialog");
            }
        });
        lView.setAdapter(adpt);
    }

    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                setApproveLeaveList(result);

                //Debugging
              /*  for(LeaveTransaction x: result)
                    Toast.makeText(getActivity().getApplicationContext(), String.valueOf(x.getId()), Toast.LENGTH_SHORT).show();*/

                setApproveLeaveList();
                } else {
                noLeaveHistory.setText("No leaves to be approved");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return getApproveLeavesTaskList();

        }

        public List<LeaveTransaction> getApproveLeavesTaskList() {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/getPendingLeaveRequestsByRoleOfUser?username={empUsername}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, empUsername);
            LeaveTransaction[] leaveArray = response.getBody();
            List<LeaveTransaction> result = Arrays.asList(leaveArray);
            return result;
        }
    }

    private class GetLeaveTransactionTask extends AsyncTask<String, Void, LeaveTransaction> {

        TaskListener mListener;

        public GetLeaveTransactionTask(TaskListener mListener) {
            this.mListener = mListener;
        }

        @Override
        protected void onPostExecute(LeaveTransaction result) {
            super.onPostExecute(result);
            selectedLeaveRequest = result;
            mListener.onTaskCompleted();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LeaveTransaction doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/findById?id={leaveTransactionId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<LeaveTransaction> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction.class, leaveTransactionId);

            return response.getBody();
        }

    }

    private class SaveLeaveApprovalDecisionsTask extends AsyncTask<String, Void, LeaveFlowDecisionsTaken> {

        private LeaveFlowDecisionsTaken leaveFlowDecisions;

        public SaveLeaveApprovalDecisionsTask(LeaveFlowDecisionsTaken decisions) {
            leaveFlowDecisions = decisions;
        }

        @Override
        protected void onPostExecute(LeaveFlowDecisionsTaken result) {
            super.onPostExecute(result);
            selectedLeaveRequest.setDecisionsBean(leaveFlowDecisions);
            selectedLeaveRequest.setLastModifiedBy(getUser().getUsername());

            if(leaveProcessCompleted){
                // Getting the latest yearly balance and do the operations on it.
                if (Leave.TIMEINLIEU.equalsName(selectedLeaveRequest.getLeaveType().getName())) {
                    FindByEmployeeNameAndTypeIdTask findLeaveType = new FindByEmployeeNameAndTypeIdTask(Leave.ANNUAL.toString(), selectedLeaveRequest.getEmployee().getEmployeeType().getId());
                    findLeaveType.execute();
                } else{
                    FindByEmployeeAndLeaveTypeTask findEntitlement = new FindByEmployeeAndLeaveTypeTask(selectedLeaveRequest.getEmployee().getId(), selectedLeaveRequest.getLeaveType().getId());
                    findEntitlement.execute();
                }
            }

            ProcessAppliedLeaveOfEmployeeTask processAppliedLeaveOfEmployee = new ProcessAppliedLeaveOfEmployeeTask();
            processAppliedLeaveOfEmployee.execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LeaveFlowDecisionsTaken doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/saveLeaveApprovalDecisions";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(leaveFlowDecisions, headers);
            ResponseEntity<LeaveFlowDecisionsTaken> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveFlowDecisionsTaken.class);

            return response.getBody();
        }

    }

    private class FindByEmployeeNameAndTypeIdTask extends AsyncTask<String, Void, LeaveType> {

        String name;
        int employeeTypeId;

        public FindByEmployeeNameAndTypeIdTask(String name, int employeeTypeId){
            this.name = name;
            this.employeeTypeId = employeeTypeId;
        }

        @Override
        protected void onPostExecute(LeaveType result) {
            super.onPostExecute(result);
            LeaveType leaveType = result;
            FindByEmployeeAndLeaveTypeTask findYearlyEntitlement = new FindByEmployeeAndLeaveTypeTask(selectedLeaveRequest.getEmployee().getId(), leaveType.getId());
            findYearlyEntitlement.execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LeaveType doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/findByEmployeeNameAndTypeId?name={name}&employeeTypeId={employeeTypeId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<LeaveType> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveType.class, name, employeeTypeId);

            return response.getBody();
        }

    }

    private class ProcessAppliedLeaveOfEmployeeTask extends AsyncTask<String, Void, LeaveTransaction> {

        @Override
        protected void onPostExecute(LeaveTransaction result) {
            super.onPostExecute(result);
            // Updating the Status in the Transaction table
            leaveTransactionPersist = result;
            UpdateLeaveBalancesOnceApprovedTask updateLeaveBalancesOnceApprovedTask = new UpdateLeaveBalancesOnceApprovedTask();
            updateLeaveBalancesOnceApprovedTask.execute();
            // sending email to notify
            //LeaveApplicationEmailNotificationService.sendingIntimationEmail(leaveTransactionPersist);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LeaveTransaction doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/processAppliedLeaveOfEmployee";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(selectedLeaveRequest, headers);
            ResponseEntity<LeaveTransaction> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveTransaction.class);

            return response.getBody();
        }

    }

    private class FindByEmployeeAndLeaveTypeTask extends AsyncTask<String, Void, YearlyEntitlement> {
        protected int employeeId, leaveTypeId;

        public FindByEmployeeAndLeaveTypeTask(int employeeId, int leaveTypeId){
            this.employeeId = employeeId;
            this.leaveTypeId = leaveTypeId;
        }
        @Override
        protected void onPostExecute(YearlyEntitlement result) {
            super.onPostExecute(result);
            entitlementBean = result;

            if (entitlementBean != null)
                selectedLeaveRequest.setYearlyLeaveBalance(entitlementBean.getYearlyLeaveBalance());
            // Saving the current state of the Leave Transaction
            ProcessAppliedLeaveOfEmployeeTask process = new ProcessAppliedLeaveOfEmployeeTask();
            process.execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected YearlyEntitlement doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/yearlyEntitlement/findByEmployeeAndLeaveType?employeeId={employeeId}&leaveTypeId={leaveTypeId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(selectedLeaveRequest, headers);
            ResponseEntity<YearlyEntitlement> response = restTemplate.exchange(url, HttpMethod.POST, request, YearlyEntitlement.class, employeeId, leaveTypeId);

            return response.getBody();
        }

    }

    private class UpdateLeaveBalancesOnceApprovedTask  extends AsyncTask<String, Void, Boolean>{

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getActivity().getApplicationContext(), "LEAVE REJECTED", Toast.LENGTH_SHORT).show();
            rej.dismiss();
            PopulateApproveLeaveTaskList repopulate = new PopulateApproveLeaveTaskList();
            repopulate.execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // The connection URL
            String url;
            if(isApproved)
                url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/updateLeaveBalancesOnceApproved";
            else
                url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/updateLeaveBalancesOnceRejected";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(leaveTransactionPersist, headers);
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, request, Boolean.class);
            return response.getBody();
        }

    }

    public void setInsertDeleted(boolean insertDeleted) {
        this.insertDeleted = insertDeleted;
    }

}

