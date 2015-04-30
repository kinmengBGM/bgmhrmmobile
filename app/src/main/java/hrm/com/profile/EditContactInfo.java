package hrm.com.profile;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Employee;
import hrm.com.model.Users;
import hrm.com.webservice.EmployeeWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditContactInfo extends Fragment {

    private Employee employee;
    private Users user;

    private EditText editPersonalPhone, editWorkPhone, editPersonalEmail, editWorkEmail;

    private EmployeeWS employeeWS;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_edit, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_contact_info, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Edit Basic Info");
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);


        employee = ((HomeActivity)getActivity()).getActiveEmployee();
        String username = ((HomeActivity)getActivity()).getUsername();
        String password = ((HomeActivity)getActivity()).getPassword();
        user = ((HomeActivity)getActivity()).getActiveUser();

        employeeWS = new EmployeeWS(username, password);

        initLayout(rootView);
        initValues();

        return rootView;
    }
    private void initLayout(View rootView){
        editPersonalEmail = (EditText) rootView.findViewById(R.id.editPersonalEmail);
        editWorkEmail = (EditText) rootView.findViewById(R.id.editWorkEmail);
        editPersonalPhone = (EditText) rootView.findViewById(R.id.editPersonalPhone);
        editWorkPhone = (EditText) rootView.findViewById(R.id.editWorkPhone);
    }

    private void initValues(){
        editPersonalEmail.setText(employee.getPersonalEmailAddress());
        editWorkEmail.setText(employee.getWorkEmailAddress());
        editPersonalPhone.setText(employee.getPersonalPhone());
        editWorkPhone.setText(employee.getOfficePhone());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case R.id.action_save:
                UpdateProfileTask update = new UpdateProfileTask();
                update.execute();
                Toast.makeText(getActivity().getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    private class UpdateProfileTask extends AsyncTask<String, Void, Employee> {

        private Employee toUpdateEmployee;

        @Override
        protected void onPostExecute(Employee result){
            super.onPostExecute(result);
            employee = result;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            toUpdateEmployee = new Employee();
            toUpdateEmployee = employee;

            toUpdateEmployee.setPersonalPhone(editPersonalPhone.getText().toString());
            toUpdateEmployee.setOfficePhone(editWorkPhone.getText().toString());

            toUpdateEmployee.setPersonalEmailAddress(editPersonalEmail.getText().toString());
            toUpdateEmployee.setWorkEmailAddress(editWorkEmail.getText().toString());

            toUpdateEmployee.setLastModifiedBy(user.getUsername());
            toUpdateEmployee.setLastModifiedTime(new Date());
        }

        @Override
        protected Employee doInBackground(String... params) {
            return employeeWS.update(toUpdateEmployee);
        }
    }

}
