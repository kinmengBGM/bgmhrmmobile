package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.model.Employee;
import hrm.com.model.Users;

/**
 * Created by Beans on 4/29/2015.
 */
public class Login {
    RestTemplate restTemplate;
    HttpEntity<String> request;
    ResponseEntity<Users[]> response;
    String username;

    public Login(String username, String password) {
        this.username = username;

        CustomRestTemplate customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();

        request = new HttpEntity<String>(customRT.getHeaders());

    }

    public void doLogin(){
        Users tempUser = getUser();
        getEmployee(tempUser.getId());
    }

    public Users getUser() throws HttpClientErrorException {
        String YOUR_URL = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/users/findUsersByUsername?username={YOUR_USERNAME}";

        response = restTemplate.exchange(YOUR_URL, HttpMethod.GET, request, Users[].class, username);
        Users[] user = response.getBody();
        Users activeUser = user[0];

        return activeUser;

    }

    public Employee getEmployee(int userId) {
        // The connection URL
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/employee/findByUserId?userId={id}";

        ResponseEntity<Employee> response = restTemplate.exchange(url, HttpMethod.GET, request, Employee.class, userId);
        Employee activeEmployee = response.getBody();

        return activeEmployee;
    }


}
