package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.hrmprototype.App;
import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/30/2015.
 */
public class RoleWS {
    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public RoleWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();
    }

    public List<String> getRolesByUsername(){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/role/findRoleNamesByUsername?username={username}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<String[]> roleArray = restTemplate.exchange(url, HttpMethod.GET, request, String[].class, username);

        List<String> result = Arrays.asList(roleArray.getBody());

        return result;

    }
}
