package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 5/5/2015.
 */
public class LeaveApplicationEmailNotificationWS {
    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public LeaveApplicationEmailNotificationWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();
    }

    public Void sendingIntimationEmail(LeaveTransaction leavePersistBean){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationEmailNotification/sendingIntimationEmail";
        HttpEntity request = new HttpEntity(leavePersistBean, customRT.getHeaders());
        restTemplate.exchange(url, HttpMethod.POST, request, Void.class);

        return null;
    }
}
