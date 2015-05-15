package hrm.com.profile;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import hrm.com.custom.fragment.DatePickerFragment;
import hrm.com.custom.listener.DatePickerListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.model.Employee;
import hrm.com.webservice.EmployeeWS;
import hrm.com.wrapper.UpdateEmployeeWrapper;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditWorkInfo extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Employee employee;

    private int grade, employeeType, department;

    private EditText editEmployeeNo;
    private EditText editPosition;
    private EditText editJoinDate;
    private EditText editResDate;
    private Spinner editGrade, editEmployeeType, editDepartment;

    private List<Address> existingAddress;
    private EmployeeWS employeeWS;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity) getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_edit, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_work_info, container, false);

        ((HomeActivity) getActivity()).enableNavigationDrawer(false);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Edit Work Info");
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);

        employee = ((HomeActivity) getActivity()).getActiveEmployee();
        String username = ((HomeActivity) getActivity()).getUsername();
        String password = ((HomeActivity) getActivity()).getPassword();

        employeeWS = new EmployeeWS(username, password);

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

        if (employee.getEmployeeGrade() != null) {
            value = employee.getEmployeeGrade().getDescription();
            adapter = (ArrayAdapter) editGrade.getAdapter();
            spinnerPosition = adapter.getPosition(value);
            editGrade.setSelection(spinnerPosition);
        }
        if (employee.getDepartment() != null) {
            value = employee.getDepartment().getDescription();
            adapter = (ArrayAdapter) editDepartment.getAdapter();
            spinnerPosition = adapter.getPosition(value);
            editDepartment.setSelection(spinnerPosition);
        }
        if (employee.getEmployeeType() != null) {
            value = employee.getEmployeeType().getDescription();
            adapter = (ArrayAdapter) editEmployeeType.getAdapter();
            spinnerPosition = adapter.getPosition(value);
            editEmployeeType.setSelection(spinnerPosition);
        }
        existingAddress = new ArrayList<Address>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (StringUtils.isBlank(editEmployeeNo.getText()) || StringUtils.isBlank(editPosition.getText())) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_field_validation, Toast.LENGTH_SHORT).show();
                    return true;
                }
                UpdateProfileTask update = new UpdateProfileTask();
                update.execute();
                return true;
        }
        return false;
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
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        private UpdateEmployeeWrapper updateEmployeeWrapper;
        private Employee toUpdateEmployee;
        private HashMap<Integer, Address> newAddressMap;
        private boolean success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            newAddressMap = new HashMap<Integer, Address>();
            toUpdateEmployee = new Employee();
            toUpdateEmployee = employee;

            dialog.setMessage("Updating profile...");
            dialog.setCancelable(false);
            dialog.show();

            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date joinDate = null, resDate = null;
            try {
                if (StringUtils.isNotEmpty(editJoinDate.getText())) {
                    joinDate = format.parse(editJoinDate.getText().toString());
                    toUpdateEmployee.setJoinDate(joinDate);
                }
                if (StringUtils.isNotEmpty(editResDate.getText())) {
                    resDate = format.parse(editResDate.getText().toString());
                    toUpdateEmployee.setResignationDate(resDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            toUpdateEmployee.setEmployeeNumber(editEmployeeNo.getText().toString());
            toUpdateEmployee.setPosition(editPosition.getText().toString());
            updateEmployeeWrapper = new UpdateEmployeeWrapper(toUpdateEmployee, grade, employeeType, department, null, existingAddress, newAddressMap);
        }

        @Override
        protected Employee doInBackground(String... params) {
            try {
                success = true;
                return employeeWS.updateEmployee(updateEmployeeWrapper);
            }catch(Exception e){
                success = false;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Employee employee) {
            super.onPostExecute(employee);
            if(success){
                Toast.makeText(getActivity().getApplicationContext(), R.string.info_profileUpdated, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
            else
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}

