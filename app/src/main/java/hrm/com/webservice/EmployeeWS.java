package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.model.Employee;

/**
 * Created by Beans on 4/30/2015.
 */
public class EmployeeWS {
    RestTemplate restTemplate;

    CustomRestTemplate customRT;

    public EmployeeWS(String username, String password) {
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();

    }

    public Employee update(Employee toUpdateEmployee) {
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/employee/update";
        HttpEntity request = new HttpEntity(toUpdateEmployee, customRT.getHeaders());
        ResponseEntity<Employee> response = restTemplate.exchange(url, HttpMethod.POST, request, Employee.class);

        return response.getBody();

    }
}