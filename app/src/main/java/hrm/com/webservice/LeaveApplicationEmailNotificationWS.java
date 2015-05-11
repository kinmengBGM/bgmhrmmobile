package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.hrmprototype.App;
import hrm.com.hrmprototype.R;
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
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/leaveApplicationEmailNotification/sendingIntimationEmail";
        HttpEntity request = new HttpEntity(leavePersistBean, customRT.getHeaders());
        restTemplate.exchange(url, HttpMethod.POST, request, Void.class);

        return null;
    }
}
