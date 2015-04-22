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

import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.model.Employee;
import hrm.com.model.Users;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAddress extends Fragment {

    private Employee employee;
    private Users user;
    private int addressId;

    private String username, password;

    private EditText editLine1, editLine2, editLine3, editPostcode, editCity, editState, editCountry;

    private Spinner addressType;

    protected Address toUpdateAddress;

    public EditAddress() {
        // Required empty public constructor
    }

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
        //((HomeActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Edit Address Info");
        setHasOptionsMenu(true);

        employee = ((HomeActivity)getActivity()).getActiveEmployee();
        username = ((HomeActivity)getActivity()).getUsername();
        password = ((HomeActivity)getActivity()).getPassword();
        user = ((HomeActivity)getActivity()).getActiveUser();
        addressId = ((HomeActivity)getActivity()).getAddressId();

        initLayout(rootView);
        GetToUpdateAddressTask getAddress = new GetToUpdateAddressTask();
        getAddress.execute();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case R.id.action_save:
                UpdateAddressTask update = new UpdateAddressTask();
                update.execute();
                Toast.makeText(getActivity().getApplicationContext(), "Address Updated", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }


    private void initLayout(View rootView){
        editLine1 = (EditText) rootView.findViewById(R.id.editLine1);
        editLine2 = (EditText) rootView.findViewById(R.id.editLine2);
        editLine3 = (EditText) rootView.findViewById(R.id.editLine3);
        editPostcode = (EditText) rootView.findViewById(R.id.editPostcode);
        editCity = (EditText) rootView.findViewById(R.id.editCity);
        editState = (EditText) rootView.findViewById(R.id.editState);
        editCountry = (EditText) rootView.findViewById(R.id.editCountry);
        addressType = (Spinner) rootView.findViewById(R.id.editAddressType);

    }

    private void initValues(){
        editLine1.setText(toUpdateAddress.getLine1());
        editLine2.setText(toUpdateAddress.getLine2());
        editLine3.setText(toUpdateAddress.getLine3());
        editPostcode.setText(toUpdateAddress.getPostcode());
        editCity.setText(toUpdateAddress.getCity());
        editState.setText(toUpdateAddress.getState());
        editCountry.setText(toUpdateAddress.getCountry());

        ArrayAdapter adapter;
        int spinnerPosition;
        String value = toUpdateAddress.getAddressType();

        adapter = (ArrayAdapter) addressType.getAdapter();
        spinnerPosition = adapter.getPosition(value);
        addressType.setSelection(spinnerPosition);

    }

    public void setToUpdateAddress(Address toUpdateAddress){this.toUpdateAddress=toUpdateAddress;}

    private class GetToUpdateAddressTask extends AsyncTask<String, Void, Address> {


        @Override
        protected void onPostExecute(Address result) {
            //super.onPostExecute(result);
            setToUpdateAddress(result);
            initValues();
        }

        @Override
        protected Address doInBackground(String... params) {
            return getAddress();

        }

        public Address getAddress() {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/findById?id={addressId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.GET, request, Address.class, addressId);
            toUpdateAddress = response.getBody();
            return toUpdateAddress;
        }
    }


    private class UpdateAddressTask extends AsyncTask<String, Void, Address>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            toUpdateAddress.setAddressType(addressType.getSelectedItem().toString());
            toUpdateAddress.setLine1(editLine1.getText().toString());
            toUpdateAddress.setLine2(editLine2.getText().toString());
            toUpdateAddress.setLine3(editLine3.getText().toString());
            toUpdateAddress.setPostcode(editPostcode.getText().toString());
            toUpdateAddress.setCity(editCity.getText().toString());
            toUpdateAddress.setState(editState.getText().toString());
            toUpdateAddress.setCountry(editCountry.getText().toString());
            toUpdateAddress.setLastModifiedBy(((HomeActivity)getActivity()).getActiveUser().getUsername());
            toUpdateAddress.setLastModifiedTime(new java.util.Date());
        }

        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);
        }

        @Override
        protected Address doInBackground(String... params) {
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/update";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(toUpdateAddress, headers);
            ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.POST, request, Address.class);
            toUpdateAddress = response.getBody();
            return toUpdateAddress;
        }
    }




}
