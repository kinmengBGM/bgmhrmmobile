package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 4/30/2015.
 */
public class LeaveApplicationFlowWS {
    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public LeaveApplicationFlowWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();

    }

    public List<LeaveTransaction> getPendingLeaveRequestsByRoleOfUser(){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/getPendingLeaveRequestsByRoleOfUser?username={empUsername}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<LeaveTransaction[]> leaveArray = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, username);

        return Arrays.asList(leaveArray.getBody());

    }

    public Boolean UpdateLeaveBalancesOnceApprovedTask(Boolean isApproved, LeaveTransaction leaveTransactionPersist){
        String url;
        if (isApproved)
            url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/updateLeaveBalancesOnceApproved";
        else
            url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/updateLeaveBalancesOnceRejected";


        HttpEntity request = new HttpEntity(leaveTransactionPersist, customRT.getHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, request, Boolean.class).getBody();


    }

}
