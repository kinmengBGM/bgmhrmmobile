package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
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
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/getAllFutureLeavesAppliedByEmployee?employeeId={userId}&todayDate={todayDate}";

        java.util.Date utilDate = new java.util.Date();
        Date todayDate = new Date(utilDate.getTime());
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, userId, todayDate);
        LeaveTransaction[] leaveArray = response.getBody();
        return Arrays.asList(leaveArray);
    }

    public List<LeaveTransaction> getAllLeavesAppliedByEmployee(int userId){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/getAllLeavesAppliedByEmployee?employeeId={userId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, userId);
        LeaveTransaction[] leaveArray = response.getBody();
        return Arrays.asList(leaveArray);
    }

    public LeaveRuleBean getLeaveRuleByRoleAndLeaveType(GetLeaveRuleByRoleAndLeaveTypeWrapper leaveRuleWrapper){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/getLeaveRuleByRoleAndLeaveType";
        HttpEntity request = new HttpEntity(leaveRuleWrapper, customRT.getHeaders());


        ResponseEntity<LeaveRuleBean> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveRuleBean.class);
        return response.getBody();
    }

    public LeaveFlowDecisionsTaken saveLeaveApprovalDecisions(){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/saveLeaveApprovalDecisions";
        HttpEntity request = new HttpEntity(null, customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.POST, request, LeaveFlowDecisionsTaken.class).getBody();
    }

    public LeaveTransaction processAppliedLeaveOfEmployee(LeaveTransaction leaveTransaction){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/processAppliedLeaveOfEmployee";
        HttpEntity request = new HttpEntity(leaveTransaction, customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.POST, request, LeaveTransaction.class).getBody();
    }

    public LeaveTransaction findById(int leaveTransactionId){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/findById?id={leaveTransactionId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction.class, leaveTransactionId).getBody();
    }

    public LeaveFlowDecisionsTaken saveLeaveApprovalDecisions(LeaveFlowDecisionsTaken leaveFlowDecisions){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/saveLeaveApprovalDecisions";
        HttpEntity request = new HttpEntity(leaveFlowDecisions, customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.POST, request, LeaveFlowDecisionsTaken.class).getBody();
    }




}
