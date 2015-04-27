package hrm.com.custom.rest;

import android.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by Beans on 4/26/2015.
 */
public class BasicAuthInterceptor implements ClientHttpRequestInterceptor {

    private final String username;
    private final String password;

    public BasicAuthInterceptor( String username, String password ) {
        this.username = username;
        this.password = password;
    }

    @Override
    public ClientHttpResponse intercept( HttpRequest request, byte[] body, ClientHttpRequestExecution execution ) throws IOException {

        //Build the auth-header
        final String auth = username + ":" + password;
        String base64EncodedCredentials = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);

        //Add the auth-header
        HttpHeaders headers = request.getHeaders();
        headers.add("Authorization", "Basic " + base64EncodedCredentials);


        return execution.execute(request, body);
    }

}
