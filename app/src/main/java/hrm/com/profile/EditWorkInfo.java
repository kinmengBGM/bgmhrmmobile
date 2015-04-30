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
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hrm.com.custom.fragment.DatePickerFragment;
import hrm.com.custom.listener.DatePickerListener;
import hrm.com.custom.listener.TaskListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.model.Employee;
import hrm.com.model.Users;
import hrm.com.wrapper.UpdateEmployeeWrapper;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditWorkInfo extends Fragment implements AdapterView.OnItemSelectedListener, TaskListener, View.OnClickListener {

    private Employee employee;
    private Users user;

    private String username, password;
    private int grade, employeeType, department;

    private EditText editEmployeeNo;
    private EditText editPosition;
    private EditText editJoinDate;
    private EditText editResDate;
    private Spinner editGrade, editEmployeeType, editDepartment;

    private List<Address> existingAddress;

    public EditWorkInfo() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity) getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_edit, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {/*
        // Inflate the layout for this fragment
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        ActionBar actionBar = ((HomeActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);*/

        View rootView = inflater.inflate(R.layout.fragment_edit_work_info, container, false);

        ((HomeActivity) getActivity()).enableNavigationDrawer(false);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Edit Basic Info");
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);

        employee = ((HomeActivity) getActivity()).getActiveEmployee();
        ((HomeActivity) getActivity()).setActiveEmployee(employee);
        username = ((HomeActivity) getActivity()).getUsername();
        password = ((HomeActivity) getActivity()).getPassword();
        user = ((HomeActivity) getActivity()).getActiveUser();
        initLayout(rootView);
        initValues();

        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.editDepartment:
                department = position + 1;
                break;
            case R.id.editGrade:
                grade = position + 1;
                break;
            case R.id.editEmployeeType:
                employeeType = position + 1;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initLayout(View rootView) {

        editEmployeeNo = (EditText) rootView.findViewById(R.id.editEmployeeNo);
        editPosition = (EditText) rootView.findViewById(R.id.editPosition);
        editJoinDate = (EditText) rootView.findViewById(R.id.editJoinDate);
        editResDate = (EditText) rootView.findViewById(R.id.editResDate);

        editGrade = (Spinner) rootView.findViewById(R.id.editGrade);
        editEmployeeType = (Spinner) rootView.findViewById(R.id.editEmployeeType);
        editDepartment = (Spinner) rootView.findViewById(R.id.editDepartment);

        editEmployeeType.setOnItemSelectedListener(this);
        editDepartment.setOnItemSelectedListener(this);
        editGrade.setOnItemSelectedListener(this);

        editJoinDate.setOnClickListener(this);
        editResDate.setOnClickListener(this);
    }

    public void initValues() {


        editEmployeeNo.setText(employee.getEmployeeNumber());
        editPosition.setText(employee.getPosition());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        if (employee.getJoinDate() != null)
            editJoinDate.setText(dateFormat.format(employee.getJoinDate()).toString());
        if (employee.getResignationDate() != null)
            editResDate.setText(dateFormat.format(employee.getResignationDate()).toString());

        String value = "";
        ArrayAdapter adapter;
        int spinnerPosition;

        value = employee.getEmployeeGrade().getDescription();
        adapter = (ArrayAdapter) editGrade.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        editGrade.setSelection(spinnerPosition);

        value = employee.getDepartment().getDescription();
        adapter = (ArrayAdapter) editDepartment.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        editDepartment.setSelection(spinnerPosition);

        value = employee.getEmployeeType().getDescription();
        adapter = (ArrayAdapter) editEmployeeType.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        editEmployeeType.setSelection(spinnerPosition);

        existingAddress = new ArrayList<Address>();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case R.id.action_save:
                //called when the up affordance/carat in actionbar is pressed

                UpdateProfileTask update = new UpdateProfileTask();
                update.execute();
                Toast.makeText(getActivity().getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public void onTaskCompleted() {

        ((HomeActivity) getActivity()).setActiveEmployee(employee);
        getActivity().onBackPressed();

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.editJoinDate:
            case R.id.editResDate:

                final TextView activeTextView;

                if (v.getId() == R.id.editJoinDate)
                    activeTextView = editJoinDate;
                else
                    activeTextView = editResDate;

                DatePickerFragment dialog = new DatePickerFragment(new DatePickerListener() {
                    @Override
                    public void onDateSelected(String date) {
                        activeTextView.setText(date);
                    }
                });

                dialog.show(getActivity().getSupportFragmentManager(), "MyDatePickerDialog");
            break;
        }
    }


        private class UpdateProfileTask extends AsyncTask<String, Void, Employee> {

            private UpdateEmployeeWrapper updateEmployeeWrapper;
            private Employee toUpdateEmployee;
            private HashMap<Integer, Address> newAddressMap;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                newAddressMap = new HashMap<Integer, Address>();
                toUpdateEmployee = new Employee();
                toUpdateEmployee = employee;

                toUpdateEmployee.setEmployeeNumber(editEmployeeNo.getText().toString());
                toUpdateEmployee.setPosition(editPosition.getText().toString());

                updateEmployeeWrapper = new UpdateEmployeeWrapper(toUpdateEmployee, grade, employeeType, department, null, existingAddress, newAddressMap);
            }

            @Override
            protected void onPostExecute(Employee result) {
                onTaskCompleted();
            }


            @Override
            protected Employee doInBackground(String... params) {
                // The connection URL
                String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/employee/updateEmployee";
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                String plainCreds = username + ":" + password;
                String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
                headers.add("Authorization", "Basic " + base64EncodedCredentials);

                // Add the String message converter
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                HttpEntity request = new HttpEntity(updateEmployeeWrapper, headers);
                ResponseEntity<Employee> response = restTemplate.exchange(url, HttpMethod.POST, request, Employee.class);
                return response.getBody();
            }
        }
    }

