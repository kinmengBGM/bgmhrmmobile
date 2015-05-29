package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.hrmprototype.App;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveFlowDecisionsTaken;
import hrm.com.model.LeaveRuleBean;
import hrm.com.model.LeaveTransaction;
import hrm.com.wrapper.GetLeaveRuleByRoleAndLeaveTypeWrapper;

/**
 * Created by Beans on 4/30/2015.
 */
public class LeaveTransactionWS {

    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public LeaveTransactionWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();
    }


    public List<LeaveTransaction> getAllFutureLeavesAppliedByEmployee(int userId){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/getAllFutureLeavesAppliedByEmployee?employeeId={userId}&todayDate={todayDate}";

        java.util.Date utilDate = new java.util.Date();
        Date todayDate = new Date(utilDate.getTime());
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, userId, todayDate);
        LeaveTransaction[] leaveArray = response.getBody();
        return Arrays.asList(leaveArray);
    }

    public List<LeaveTransaction> getAllLeavesAppliedByEmployee(int userId){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/getAllLeavesAppliedByEmployee?employeeId={userId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, userId);
        LeaveTransaction[] leaveArray = response.getBody();
        return Arrays.asList(leaveArray);
    }

    public LeaveRuleBean getLeaveRuleByRoleAndLeaveType(GetLeaveRuleByRoleAndLeaveTypeWrapper leaveRuleWrapper){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/getLeaveRuleByRoleAndLeaveType";
        HttpEntity request = new HttpEntity(leaveRuleWrapper, customRT.getHeaders());

        ResponseEntity<LeaveRuleBean> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveRuleBean.class);
        return response.getBody();
    }

    public LeaveFlowDecisionsTaken saveLeaveApprovalDecisions(){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/saveLeaveApprovalDecisions";
        HttpEntity request = new HttpEntity(null, customRT.getHeaders());
        ResponseEntity<LeaveFlowDecisionsTaken> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveFlowDecisionsTaken.class);

        return response.getBody();
    }

    public LeaveTransaction processAppliedLeaveOfEmployee(LeaveTransaction leaveTransaction){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/processAppliedLeaveOfEmployee";
        HttpEntity request = new HttpEntity(leaveTransaction, customRT.getHeaders());
        ResponseEntity<LeaveTransaction> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveTransaction.class);

        return response.getBody();
    }

    public LeaveTransaction findById(int leaveTransactionId){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/findById?id={leaveTransactionId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction.class, leaveTransactionId).getBody();
    }

    public LeaveFlowDecisionsTaken saveLeaveApprovalDecisions(LeaveFlowDecisionsTaken leaveFlowDecisions){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveTransaction/saveLeaveApprovalDecisions";
        HttpEntity request = new HttpEntity(leaveFlowDecisions, customRT.getHeaders());
        ResponseEntity<LeaveFlowDecisionsTaken> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveFlowDecisionsTaken.class);

        return response.getBody();
    }




}
