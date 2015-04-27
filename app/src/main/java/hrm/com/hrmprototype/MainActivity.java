package hrm.com.hrmprototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import hrm.com.model.Employee;
import hrm.com.model.Users;


public class MainActivity extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private Button button;

    private Users activeUser;
    private String YOUR_USERNAME;
    private String YOUR_PASSWORD;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webService web = new webService();
                web.execute();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private class webService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... s) {

            // Create a new RestTemplate instance

            try {
                YOUR_USERNAME = username.getText().toString();
                YOUR_PASSWORD = password.getText().toString();
                String YOUR_URL = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/users/findUsersByUsername?username={YOUR_USERNAME}";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                String plainCreds = YOUR_USERNAME + ":" + YOUR_PASSWORD;
                String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
                headers.add("Authorization", "Basic " + base64EncodedCredentials);

                // Add the String message converter
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<Users[]> response = restTemplate.exchange(YOUR_URL, HttpMethod.GET, request, Users[].class, YOUR_USERNAME);
                Users[] user = response.getBody();
                activeUser = user[0];
                userId = user[0].getId();
                return "Logged in as " + user[0].getUsername();
            } catch (HttpClientErrorException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null) {
                GetEmployeeTask getEmployee = new GetEmployeeTask();
                getEmployee.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetEmployeeTask extends AsyncTask<String, Void, Employee> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private Employee activeEmployee;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Logging in...");
            dialog.show();
            activeEmployee = new Employee();
        }

        @Override
        protected Employee doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/employee/findByUserId?userId={id}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = YOUR_USERNAME + ":" + YOUR_PASSWORD;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<Employee> response = restTemplate.exchange(url, HttpMethod.GET, request, Employee.class, userId);
            activeEmployee = response.getBody();

            return activeEmployee;
        }

        @Override
        protected void onPostExecute(Employee result) {
            dialog.dismiss();
            Intent intent = new Intent("hrm.com.hrmprototype.HomeActivity");
            intent.putExtra("userId", userId);
            intent.putExtra("username", YOUR_USERNAME);
            intent.putExtra("password", YOUR_PASSWORD);
            intent.putExtra("user", activeUser);
            intent.putExtra("employee", result);
            startActivity(intent);
        }

    }

}
