package hrm.com.hrmprototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.web.client.HttpClientErrorException;

import hrm.com.webservice.Login;


public class MainActivity extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private Button login;

    private String YOUR_USERNAME;
    private String YOUR_PASSWORD;

    private Login newLogin;

    private enum LoginVal{SUCCESS, ERROR_CREDENTIAL, ERROR_SERVER}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                YOUR_USERNAME = username.getText().toString();
                YOUR_PASSWORD = password.getText().toString();
                newLogin = new Login(YOUR_USERNAME, YOUR_PASSWORD);
                GetUserTask getUserTask = new GetUserTask();
                getUserTask.execute();
            }
        });
    }

    private class GetUserTask extends AsyncTask<String, Void, LoginVal> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Logging in...");
            dialog.show();
        }

        @Override
        protected LoginVal doInBackground(String... s) {
            try {
                 newLogin.doLogin();
                 return LoginVal.SUCCESS;
            }catch(HttpClientErrorException e){
                return LoginVal.ERROR_CREDENTIAL;
            }catch(Exception e){
                return LoginVal.ERROR_SERVER;
            }
        }

        @Override
        protected void onPostExecute(LoginVal results) {
            switch(results){
                case SUCCESS:
                    Intent intent = new Intent("hrm.com.hrmprototype.HomeActivity");
                    intent.putExtra("username", YOUR_USERNAME);
                    intent.putExtra("password", YOUR_PASSWORD);
                    intent.putExtra("user", newLogin.getActiveUser());
                    intent.putExtra("employee", newLogin.getActiveEmployee());

                    dialog.dismiss();
                    startActivity(intent);
                    break;
                case ERROR_CREDENTIAL:
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.error_login_validation, Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_SERVER:
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.error_timeout_validation, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
