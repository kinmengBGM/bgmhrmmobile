package hrm.com.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hrm.com.custom.adapter.AddressAdapter;
import hrm.com.custom.listener.ProfileFragmentChangeListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.model.Employee;


public class ViewProfile extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private String username;
    private String password;
    private Employee employee;
    private ListView lView;

    private ProfileFragmentChangeListener listener;

    private Button editBasic, editLocation, editContact, editWork;

    private AddressAdapter adpt;
    private List<Address> addressList = new ArrayList<Address>();
    private int addressId;

    public ViewProfile(){}

    public static ViewProfile newInstance(ProfileFragmentChangeListener listener){
        return new ViewProfile(listener);
    }

    @SuppressLint("ValidFragment")
    public ViewProfile(ProfileFragmentChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void onResume(){
        super.onResume();
        setHasOptionsMenu(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("View Profile");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("View Profile");
        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.employee = ((HomeActivity) getActivity()).getActiveEmployee();

        TextView employeeName = (TextView) rootView.findViewById(R.id.txtProfileName);
        TextView workPhone = (TextView) rootView.findViewById(R.id.txtProfileWorkPhone);
        TextView workEmail = (TextView) rootView.findViewById(R.id.txtProfileWorkEmail);
        TextView position = (TextView) rootView.findViewById(R.id.txtProfilePosition);
        TextView department = (TextView) rootView.findViewById(R.id.txtProfileDepartment);

        Button editBasic = (Button) rootView.findViewById(R.id.btnEditBasicInfo);
        Button editContact = (Button) rootView.findViewById(R.id.btnEditContactInfo);
        Button editLocation = (Button) rootView.findViewById(R.id.btnAddLocationInfo);
        Button editWork = (Button) rootView.findViewById(R.id.btnEditWorkInfo);

        editBasic.setOnClickListener(this);
        editContact.setOnClickListener(this);
        editLocation.setOnClickListener(this);
        editWork.setOnClickListener(this);

        employeeName.setText(employee.getName());
        workPhone.setText(employee.getOfficePhone());
        workEmail.setText(employee.getWorkEmailAddress());
        position.setText(employee.getPosition());
        if(employee.getDepartment().getDescription()!= null)
            department.setText(employee.getDepartment().getDescription());

        adpt = new AddressAdapter(new ArrayList<Address>(), rootView.getContext());
        lView = (ListView) rootView.findViewById(R.id.listViewAddress);

        PopulateAddressTask viewAddress = new PopulateAddressTask();
        viewAddress.execute();

        lView.setAdapter(adpt);
        lView.setOnItemClickListener(this);
        return rootView;
    }


    public void setAddressList(List<Address> addressList){
        this.addressList = addressList;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final String[] addressItems = {"Update", "Delete"};

        if (parent.getId() == R.id.listViewAddress) {
            addressId = adpt.getItem(position).getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose action");
            builder.setItems(addressItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        ((HomeActivity)getActivity()).setAddressId(addressId);
                        listener.onEditAddressInfo();
                    } else if (item == 1) {
                        Toast.makeText(getActivity().getApplicationContext(), "DELETE", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnEditBasicInfo:
                listener.onEditBasicInfo();
            break;
            case R.id.btnEditContactInfo:
                listener.onEditContactInfo();
                break;
            case R.id.btnAddLocationInfo:
                listener.onCreateAddress();
                break;
            case R.id.btnEditWorkInfo:
                listener.onEditWorkInfo();

        }
    }


    private class PopulateAddressTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPostExecute(List<Address> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                setAddressList(result);

                adpt.setItemList(result);
                adpt.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lView);
            }
            //((HomeActivity)getActivity()).setExistingAddressList(addressList);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Address> doInBackground(String... params) {
            List<Address> result = new ArrayList<Address>();
            return getAddress();

        }

        public List<Address> getAddress() {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/address/findByEmployeeId?employeeId={employeeId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<Address[]> response = restTemplate.exchange(url, HttpMethod.GET, request, Address[].class, employee.getId());
            Address[] addressArray = response.getBody();
            List<Address> result = Arrays.asList(addressArray);
            return result;
        }
    }


}