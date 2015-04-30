package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.model.Address;

/**
 * Created by Beans on 4/30/2015.
 */
public class AddressWS {
    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public AddressWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();


    }

    public List<Address> findByEmployeeId(int employeeId){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/findByEmployeeId?employeeId={employeeId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<Address[]> response = restTemplate.exchange(url, HttpMethod.GET, request, Address[].class, employeeId);

        Address[] addressArray = response.getBody();
        return Arrays.asList(addressArray);
    }

    public Address delete(int addressId){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/delete?id={addressId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.GET, request, Address.class, addressId);

        return response.getBody();
    }

    public Address findById(int addressId){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/findById?id={addressId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.GET, request, Address.class, addressId);

        return response.getBody();
    }

    public Address update(Address toUpdateAddress){
        String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/update";
        HttpEntity request = new HttpEntity(toUpdateAddress, customRT.getHeaders());

        ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.POST, request, Address.class);

        return response.getBody();
    }

}
