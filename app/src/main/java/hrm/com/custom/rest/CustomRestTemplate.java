package hrm.com.custom.rest;

import android.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Beans on 4/29/2015.
 */
public class CustomRestTemplate {

    RestTemplate restTemplate;
    HttpHeaders headers;
    String plainCreds;
    HttpEntity<String> request;

    String username, password;

    public CustomRestTemplate(String username, String password){
        this.username = username;
        this.password = password;

        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        plainCreds = username + ":" + password;

        String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
        headers.add("Authorization", "Basic " + base64EncodedCredentials);

        // Add the String message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        request = new HttpEntity<String>(headers);
    }

    public RestTemplate getRestTemplate(){
        return restTemplate;
    }

    public HttpHeaders getHeaders(){
        return headers;
    }
}
