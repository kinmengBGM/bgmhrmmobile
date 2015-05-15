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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.webservice.AddressWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAddress extends Fragment {

    private int addressId;
    private Address toUpdateAddress;

    private EditText editLine1, editLine2, editLine3, editPostcode, editCity, editState, editCountry;
    private Spinner addressType;

    private AddressWS addressWS;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_address, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Edit Address Info");
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);

        String username = ((HomeActivity)getActivity()).getUsername();
        String password = ((HomeActivity)getActivity()).getPassword();
        addressId = ((HomeActivity)getActivity()).getAddressId();

        addressWS = new AddressWS(username, password);

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
                if(StringUtils.isBlank(editLine1.getText()) || StringUtils.isBlank(editCountry.getText())) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_field_validation, Toast.LENGTH_SHORT).show();
                    return true;
                }
                UpdateAddressTask update = new UpdateAddressTask();
                update.execute();
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
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(Address result) {
            super.onPostExecute(result);
            if(result != null) {
                setToUpdateAddress(result);
                initValues();
            }
            else
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

        @Override
        protected Address doInBackground(String... params) {
            try {
                return addressWS.findById(addressId);
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Loading address information...");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void updateUiValues(){
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

    private class UpdateAddressTask extends AsyncTask<String, Void, Address>{
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Updating address information...");
            dialog.setCancelable(false);
            dialog.show();
            updateUiValues();
        }

        @Override
        protected Address doInBackground(String... params) {
            try {
                return addressWS.update(toUpdateAddress);
            }catch(Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);
            if (address!= null){
                Toast.makeText(getActivity().getApplicationContext(), "Address Updated", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

}
