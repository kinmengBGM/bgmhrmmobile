package hrm.com.hrmprototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.system.ErrnoException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.web.client.HttpClientErrorException;

import hrm.com.model.Users;
import hrm.com.webservice.Login;


public class MainActivity extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private Button button;

    private Users activeUser;
    private String YOUR_USERNAME;
    private String YOUR_PASSWORD;

    private Login newLogin;
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
                YOUR_USERNAME = username.getText().toString();
                YOUR_PASSWORD = password.getText().toString();
                newLogin = new Login(YOUR_USERNAME, YOUR_PASSWORD);
                GetUserTask getUserTask = new GetUserTask();
                getUserTask.execute();
            }
        });
    }

    private class GetUserTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Logging in...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... s) {
            try {
                 newLogin.doLogin();
                 return true;
            }catch(HttpClientErrorException e){
                return false;
            }catch (ErrnoException e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {
            if (results) {
                Intent intent = new Intent("hrm.com.hrmprototype.HomeActivity");
                intent.putExtra("userId", userId);
                intent.putExtra("username", YOUR_USERNAME);
                intent.putExtra("password", YOUR_PASSWORD);
                intent.putExtra("user", newLogin.getActiveUser());
                intent.putExtra("employee", newLogin.getActiveEmployee());

                dialog.dismiss();
                startActivity(intent);
                /*activeUser = results;
                GetEmployeeTask getEmployee = new GetEmployeeTask();
                getEmployee.execute();*/
            } else {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.error_login_validation, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
