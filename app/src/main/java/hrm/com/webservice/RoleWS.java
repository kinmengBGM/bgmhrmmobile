package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;

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
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/role/findRoleNamesByUsername?username={username}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<String[]> roleArray = restTemplate.exchange(url, HttpMethod.GET, request, String[].class, username);

        List<String> result = Arrays.asList(roleArray.getBody());

        return result;

    }
}
