package hrm.com.profile;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Employee;
import hrm.com.model.Users;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBasicDetails extends Fragment implements AdapterView.OnItemSelectedListener{


    private Employee employee;
    private Users user;

    private String username, password;

    private EditText editName;
    private EditText editIcNo;
    private EditText editPassportNo;
    private EditText editNationality;
    private Spinner editGender, editReligion, editMarital;

    private String gender, religion, marital;


    public EditBasicDetails() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_edit, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {/*
        // Inflate the layout for this fragment
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        ActionBar actionBar = ((HomeActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);*/

        View rootView = inflater.inflate(R.layout.fragment_edit_basic_details, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
         //((HomeActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Edit Basic Info");
        setHasOptionsMenu(true);

        employee = ((HomeActivity)getActivity()).getActiveEmployee();
        username = ((HomeActivity)getActivity()).getUsername();
        password = ((HomeActivity)getActivity()).getPassword();
        user = ((HomeActivity)getActivity()).getActiveUser();
        initLayout(rootView);
        initValues();

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case R.id.action_save:
                //called when the up affordance/carat in actionbar is pressed

                UpdateProfileTask update = new UpdateProfileTask();
                update.execute();
                Toast.makeText(getActivity().getApplicationContext(),"Profile Updated", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    private void initLayout(View rootView){

        editName= (EditText) rootView.findViewById(R.id.editName);
        editIcNo= (EditText) rootView.findViewById(R.id.editIcNo);
        editPassportNo= (EditText) rootView.findViewById(R.id.editPassportNo);
        editNationality= (EditText) rootView.findViewById(R.id.editNationality);

        editGender= (Spinner) rootView.findViewById(R.id.editGender);
        editReligion= (Spinner) rootView.findViewById(R.id.editReligion);
        editMarital= (Spinner) rootView.findViewById(R.id.editMarital);
        editGender.setOnItemSelectedListener(this);
        editReligion.setOnItemSelectedListener(this);
        editMarital.setOnItemSelectedListener(this);

    }
    public void initValues(){


        editName.setText(employee.getName());
        editIcNo.setText(employee.getIdNumber());
        editPassportNo.setText(employee.getPassportNumber());
        editNationality.setText(employee.getNationality());

        String value = "";
        ArrayAdapter adapter;
        int spinnerPosition;

        if(employee.getGender().equals("M"))
            value = "Male";
        else if(employee.getGender().equals("F"))
            value = "Female";
        adapter = (ArrayAdapter) editGender.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        editGender.setSelection(spinnerPosition);

        value = employee.getReligion();
        adapter = (ArrayAdapter) editReligion.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        editReligion.setSelection(spinnerPosition);

        value = employee.getMaritalStatus();
        adapter = (ArrayAdapter) editMarital.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        editMarital.setSelection(spinnerPosition);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.editGender:
                if (parent.getSelectedItem().toString().equals("Male"))
                    gender = "M";
                else if(parent.getSelectedItem().toString().equals("Female"))
                    gender = "F";
                break;
            case R.id.editReligion:
                religion = parent.getSelectedItem().toString();
                break;
            case R.id.editMarital:
                marital = parent.getSelectedItem().toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class UpdateProfileTask extends AsyncTask<String, Void, Employee> {

        private Employee toUpdateEmployee;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            toUpdateEmployee = new Employee();
            toUpdateEmployee = employee;

            toUpdateEmployee.setName(editName.getText().toString());
            toUpdateEmployee.setIdNumber(editIcNo.getText().toString());
            toUpdateEmployee.setPassportNumber(editPassportNo.getText().toString());
            toUpdateEmployee.setGender(gender);
            toUpdateEmployee.setReligion(religion);
            toUpdateEmployee.setMaritalStatus(marital);
            toUpdateEmployee.setNationality(editNationality.getText().toString());
            toUpdateEmployee.setLastModifiedBy(user.getUsername());
            toUpdateEmployee.setLastModifiedTime(new Date());
        }

        @Override
        protected Employee doInBackground(String... params) {

            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/employee/update";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(toUpdateEmployee, headers);
            ResponseEntity<Employee> response = restTemplate.exchange(url, HttpMethod.POST, request, Employee.class);
            return response.getBody();
        }
    }


}