
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
import java.util.List;
import java.util.Map;

import hrm.com.custom.adapter.ApproveLeaveAdapter;
import hrm.com.custom.fragment.RejectLeaveDialog;
import hrm.com.custom.listener.ApproveLeaveListener;
import hrm.com.custom.listener.RejectLeaveListener;
import hrm.com.custom.listener.TaskListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveFlowDecisionsTaken;
import hrm.com.model.LeaveRuleBean;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Users;
import hrm.com.wrapper.GetPendingLeaveRequestsByRoleOfUserWrapper;

/**
 * Created by Beans on 4/14/2015.
 */

@SuppressLint("ValidFragment")
public class ApproveLeaveTaskList extends Fragment{
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
    //private AuditTrail auditTrail;
    private LeaveTransaction selectedLeaveRequest;
    private Double currentLeaveBalance;
    private String param;
    private String rejectReason;
    LeaveTransaction leaveTransactionPersist;
    private boolean isApproved;
    //private StreamedContent sickLeaveAttachment;

    public ApproveLeaveTaskList() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
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

    public void setApproveLeaveList(List<LeaveTransaction> leaveHistoryList){
        this.approveLeaveList = leaveHistoryList;
    }

    public Users getUser(){
        return user;
    }


    private Map<String,Integer> getMapByLeaveRules(LeaveRuleBean leaveRule){
        Map<String,Integer> rulesMap = new HashMap<String,Integer>();
        if(StringUtils.trimToNull(leaveRule.getApproverNameLevel1())!=null)
            rulesMap.put(leaveRule.getApproverNameLevel1(), 1);
        if(StringUtils.trimToNull(leaveRule.getApproverNameLevel2())!=null)
            rulesMap.put(leaveRule.getApproverNameLevel2(), 2);
        if(StringUtils.trimToNull(leaveRule.getApproverNameLevel3())!=null)
            rulesMap.put(leaveRule.getApproverNameLevel3(), 3);
        if(StringUtils.trimToNull(leaveRule.getApproverNameLevel4())!=null)
            rulesMap.put(leaveRule.getApproverNameLevel4(), 4);
        if(StringUtils.trimToNull(leaveRule.getApproverNameLevel5())!=null)
            rulesMap.put(leaveRule.getApproverNameLevel5(), 5);
        return rulesMap;
    }

    public void doRejectLeaveRequest() {
        try {
            isApproved = Boolean.FALSE;
            GetLeaveTransactionTask getLeaveTransactionTask = new GetLeaveTransactionTask(new TaskListener(){
                @Override
                public void onTaskCompleted() {
                    LeaveRuleBean leaveRule = selectedLeaveRequest.getLeaveRuleBean();
                    leaveTransactionPersist=null;
                    //ApplLogger.getLogger().info(String.format("Leave Application is rejected by user %s with role : %s", getActorUsers().getUsername(),selectedLeaveRequest.getDecisionToBeTaken()));
                    Map<String,Integer> rulesMap = getMapByLeaveRules(leaveRule);
                    if(rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken())==1){
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel1("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel1(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s ", getActorUsers().getUsername()));
                    }else if(rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken())==2){
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel2("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel2(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    }else if(rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken())==3){
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel3("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel3(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    }else if(rulesMap.get(selectedLeaveRequest.getDecisionToBeTaken())==4){
                        selectedLeaveRequest.getDecisionsBean().setDecisionLevel4("NO");
                        selectedLeaveRequest.getDecisionsBean().setDecisionUserLevel4(getUser().getUsername());
                        //ApplLogger.getLogger().info(String.format("Current Level is rejected by : %s", getActorUsers().getUsername()));
                    }else{
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
        }catch(Exception e) {/*
            log.error("Error while approving leave by "+getActorUsers().getUsername(), e);
            FacesMessage msg = new FacesMessage("Error : "+getExcptnMesProperty(e.getMessage()),"Leave approve error");
            msg.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext.getCurrentInstance().addMessage(null, msg);*/
        }
    }

    public void setRejectReason(String rejectReason){
        this.rejectReason = rejectReason;
    }

    public void setApproveLeaveList(){

        adpt = new ApproveLeaveAdapter(getActivity().getApplicationContext(), R.layout.approve_leave_row_layout, approveLeaveList, new ApproveLeaveListener() {

            @Override
            public void onRejectSelected(int leaveId) {
                Toast.makeText(getActivity().getApplicationContext(), "REJECT", Toast.LENGTH_SHORT).show();
                leaveTransactionId = leaveId;
                RejectLeaveDialog rej = new RejectLeaveDialog(new RejectLeaveListener() {
                    @Override
                    public void onRejectLeave(String reason) {
                        setRejectReason(reason);
                        doRejectLeaveRequest();
                        Toast.makeText(getActivity().getApplicationContext(), reason + "REJECTED", Toast.LENGTH_SHORT).show();
                    }
                });
                rej.show(getFragmentManager(), "Reject Leave");
            }

            @Override
            public void onApproveSelected() {
                Toast.makeText(getActivity().getApplicationContext(), "APPROVE", Toast.LENGTH_SHORT).show();

            }
        });
        lView.setAdapter(adpt);
    }


    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                setApproveLeaveList(result);
                setApproveLeaveList();
            }
            else{
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
        public GetLeaveTransactionTask(TaskListener mListener){
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

        protected LeaveFlowDecisionsTaken leaveFlowDecisions;

        public SaveLeaveApprovalDecisionsTask(LeaveFlowDecisionsTaken leaveFlowDecisions){
            this.leaveFlowDecisions = leaveFlowDecisions;
        }

        @Override
        protected void onPostExecute(LeaveFlowDecisionsTaken result) {
            super.onPostExecute(result);
            selectedLeaveRequest.setDecisionsBean(leaveFlowDecisions);
            selectedLeaveRequest.setLastModifiedBy(getUser().getUsername());
            // Saving the current state of the Leave Transaction
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

    private class UpdateLeaveBalancesOnceApprovedTask extends AsyncTask<String, Void, Void> {

        GetPendingLeaveRequestsByRoleOfUserWrapper wrapper;
        Boolean bool;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setInsertDeleted(true);
            Toast.makeText(getActivity().getApplicationContext(), "REJECTED", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wrapper= new GetPendingLeaveRequestsByRoleOfUserWrapper(leaveTransactionPersist, isApproved);
        }

        @Override
        protected Void doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/updateLeaveBalancesOnceApproved";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(wrapper, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, request, Void.class);

            return response.getBody();
        }

    }

    public void setInsertDeleted(boolean insertDeleted) {
        this.insertDeleted = insertDeleted;
    }

}

