package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.hrmprototype.App;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 5/7/2015.
 */
public class CalendarEventWS {
    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;
    public CalendarEventWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();
    }

    public void createEventForApprovedLeave(LeaveTransaction leaveTransaction){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/calendarEvent/createEventForApprovedLeave";
        HttpEntity request = new HttpEntity(leaveTransaction, customRT.getHeaders());

        restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
    }
}
