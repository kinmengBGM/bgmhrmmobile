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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.model.Employee;
import hrm.com.model.Users;
import hrm.com.webservice.EmployeeWS;
import hrm.com.wrapper.UpdateEmployeeWrapper;

public class CreateAddress extends Fragment {

    private HashMap<Integer, Address> newAddressMap = new HashMap<Integer, Address>();
    private Address newAddress;

    private String username, password;
    private Employee employee;
    private Users user;

    private List<Address> existingAddressList = new ArrayList<Address>();

    private EmployeeWS employeeWS;

    private Spinner addressType;
    private EditText line1;
    private EditText line2;
    private EditText line3;
    private EditText postcode;
    private EditText city;
    private EditText state;
    private EditText country;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_address, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Create New Address");
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);

        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.employee = ((HomeActivity) getActivity()).getActiveEmployee();
        this.user = ((HomeActivity) getActivity()).getActiveUser();
        newAddress = new Address();

        employeeWS = new EmployeeWS(username, password);
        initLayout(rootView);

        return rootView;
    }

    public void initLayout(View rootView){
        line1 = (EditText) rootView.findViewById(R.id.editLine1);
        line2 = (EditText) rootView.findViewById(R.id.editLine2);
        line3 = (EditText) rootView.findViewById(R.id.editLine3);
        postcode = (EditText) rootView.findViewById(R.id.editPostcode);
        city = (EditText) rootView.findViewById(R.id.editCity);
        state = (EditText) rootView.findViewById(R.id.editState);
        country = (EditText) rootView.findViewById(R.id.editCountry);

        addressType = (Spinner) rootView.findViewById(R.id.editAddressType);

    }

    public void addAddressToEmployee() {
        int index = newAddressMap.size() + 9000;
        newAddress.setId(index);

        newAddress.setAddressType(addressType.getSelectedItem().toString());
        newAddress.setLine1(line1.getText().toString());
        newAddress.setLine2(line2.getText().toString());
        newAddress.setLine3(line3.getText().toString());
        newAddress.setPostcode(postcode.getText().toString());
        newAddress.setCity(city.getText().toString());
        newAddress.setState(state.getText().toString());
        newAddress.setCountry(country.getText().toString());
        newAddress.setCreatedBy(user.getUsername().toString());

        newAddress.setDeleted(false);

        Address addressToBeAdded = newAddress;
        newAddressMap.put(index, addressToBeAdded);
        //resetAddressOperation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case R.id.action_save:
                if(StringUtils.isBlank(line1.getText()) || StringUtils.isBlank(country.getText())) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_field_validation, Toast.LENGTH_SHORT).show();
                    return true;
                }
                addAddressToEmployee();
                CreateAddressTask update = new CreateAddressTask();
                update.execute();
                return true;
        }
        return false;
    }

    private class CreateAddressTask extends AsyncTask<String, Void, Employee> {

        private UpdateEmployeeWrapper toUpdateEmployee;
        private boolean success;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Adding address...");
            dialog.setCancelable(false);
            dialog.show();

            toUpdateEmployee = new UpdateEmployeeWrapper();

            toUpdateEmployee.setEmployee(employee);
            toUpdateEmployee.setEmployeeGradeId(employee.getEmployeeGrade().getId());
            toUpdateEmployee.setDepartmentId(employee.getDepartment().getId());
            toUpdateEmployee.setEmployeeTypeId(employee.getEmployeeType().getId());
            toUpdateEmployee.setUsers(null);
            toUpdateEmployee.setExistingAddressList(existingAddressList);
            toUpdateEmployee.setNewAddressMap(newAddressMap);

        }

        @Override
        protected void onPostExecute(Employee response) {
            super.onPostExecute(response);
            if(success) {
                Toast.makeText(getActivity().getApplicationContext(), "Address Created", Toast.LENGTH_SHORT).show();
                newAddressMap = new HashMap<Integer, Address>();
                existingAddressList = null;
                getActivity().onBackPressed();
            }else
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

        @Override
        protected Employee doInBackground(String... params) {
            try {
                success = true;
                return employeeWS.updateEmployee(toUpdateEmployee);
            }catch (Exception e){
                success =false;
                return null;
            }
        }
    }

}

