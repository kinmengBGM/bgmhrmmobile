package hrm.com.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hrm.com.custom.adapter.AddressAdapter;
import hrm.com.custom.listener.ProfileFragmentChangeListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Address;
import hrm.com.model.Employee;
import hrm.com.webservice.AddressWS;

@SuppressLint("ValidFragment")
public class ViewProfile extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String username;
    private String password;
    private Employee employee;

    private ListView lView;
    private RelativeLayout layoutAddressInfo;

    private ProfileFragmentChangeListener listener;

    private TextView employeeName, workPhone, workEmail, position, joinDate;

    private AddressAdapter adpt;
    private int addressId;

    private AddressWS addressWS;

    public static ViewProfile newInstance(ProfileFragmentChangeListener listener) {
        return new ViewProfile(listener);
    }

    public ViewProfile(ProfileFragmentChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        PopulateAddressTask populateAddressTask = new PopulateAddressTask();
        populateAddressTask.execute();
        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("My Profile");
        initUiValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("My Profile");
        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.employee = ((HomeActivity) getActivity()).getActiveEmployee();
        setHasOptionsMenu(true);

        addressWS = new AddressWS(username, password);

        initLayout(rootView);
        initUiValues();

        PopulateAddressTask viewAddress = new PopulateAddressTask();
        viewAddress.execute();

        lView.setAdapter(adpt);
        lView.setOnItemClickListener(this);
        return rootView;
    }

    public void initLayout(View rootView) {
        employeeName = (TextView) rootView.findViewById(R.id.txtProfileName);
        workPhone = (TextView) rootView.findViewById(R.id.txtProfileWorkPhone);
        workEmail = (TextView) rootView.findViewById(R.id.txtProfileWorkEmail);
        position = (TextView) rootView.findViewById(R.id.txtProfilePosition);
        joinDate = (TextView) rootView.findViewById(R.id.txtJoinDate);

        lView = (ListView) rootView.findViewById(R.id.listViewAddress);
        adpt = new AddressAdapter(new ArrayList<Address>(), rootView.getContext());

        layoutAddressInfo = (RelativeLayout) rootView.findViewById(R.id.layoutAddressInfo);

        Button editBasic = (Button) rootView.findViewById(R.id.btnEditBasicInfo);
        Button editContact = (Button) rootView.findViewById(R.id.btnEditContactInfo);
        Button editLocation = (Button) rootView.findViewById(R.id.btnAddLocationInfo);
        Button editWork = (Button) rootView.findViewById(R.id.btnEditWorkInfo);

        editBasic.setOnClickListener(this);
        editContact.setOnClickListener(this);
        editLocation.setOnClickListener(this);
        editWork.setOnClickListener(this);

    }

    public void initUiValues(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

        employeeName.setText(employee.getName());
        workPhone.setText(employee.getOfficePhone());
        workEmail.setText(employee.getWorkEmailAddress());
        position.setText(employee.getPosition());
        if(employee.getJoinDate() != null)
            joinDate.setText("Joined since " + dateFormat.format(employee.getJoinDate()).toString());
        else
            joinDate.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final String[] addressItems = {"Update", "Delete"};
        addressId = adpt.getItem(position).getId();
        if (parent.getId() == R.id.listViewAddress) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose action");
            builder.setItems(addressItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        ((HomeActivity) getActivity()).setAddressId(addressId);
                        listener.onEditAddressInfo();
                    } else if (item == 1) {
                        DeleteAddressTask deleteAddressTask = new DeleteAddressTask();
                        deleteAddressTask.execute();
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
        switch (v.getId()) {
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
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading addresses...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<Address> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                lView.setVisibility(View.VISIBLE);
                layoutAddressInfo.setVisibility(View.GONE);

                adpt.setItemList(result);
                adpt.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lView);
            }
            else{
                lView.setVisibility(View.GONE);
                layoutAddressInfo.setVisibility(View.VISIBLE);
            }
            dialog.dismiss();
        }

        @Override
        protected List<Address> doInBackground(String... params) {
            return addressWS.findByEmployeeId(employee.getId());
        }
    }

    private class DeleteAddressTask extends AsyncTask<String, Void, Address> {

        @Override
        protected void onPostExecute(Address deletedAddress) {
            super.onPostExecute(deletedAddress);
            Toast.makeText(getActivity().getApplicationContext(), "Address deleted", Toast.LENGTH_SHORT).show();
            PopulateAddressTask repopulate = new PopulateAddressTask();
            repopulate.execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Address doInBackground(String... params) {
            return addressWS.delete(addressId);
        }

    }


}