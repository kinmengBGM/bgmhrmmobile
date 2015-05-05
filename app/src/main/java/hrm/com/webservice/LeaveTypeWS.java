package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.model.LeaveType;

/**
 * Created by Beans on 5/5/2015.
 */
public class LeaveTypeWS {
    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public LeaveTypeWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();
    }

    public LeaveType findByEmployeeNameAndTypeId(String name, int employeeTypeId){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveType/findByEmployeeNameAndTypeId?name={name}&employeeTypeId={employeeTypeId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.GET, request, LeaveType.class, name, employeeTypeId).getBody();
    }
}
