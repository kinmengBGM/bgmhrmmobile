package hrm.com.leave;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hrm.com.custom.adapter.LeaveAdapter;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Users;

/**
 * Created by Beans on 4/7/2015.
 */
@SuppressLint("ValidFragment")
public class LeaveHistory extends Fragment{
    //private List<LeaveTransaction> leaveHistoryList = new ArrayList<LeaveTransaction>();

    private ExpandableListAdapter listAdapter;
    private List<LeaveTransaction> listData;
    private ExpandableListView lView;
    private TextView noLeaveHistory;

    private String username;
    private String password;
    private Users user;
    private int userId;

    public LeaveHistory() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.user = ((HomeActivity) getActivity()).getActiveUser();
        this.userId = user.getId();

        lView = (ExpandableListView) rootView.findViewById(R.id.listViewAddress);
        noLeaveHistory = (TextView) rootView.findViewById(R.id.textView);

        PopulateLeaveHistoryTask populateLeaveHistoryTask = new PopulateLeaveHistoryTask();
        populateLeaveHistoryTask.execute();

        return rootView;
    }

    public void setListAdapter(List<LeaveTransaction> result){
        listData = new ArrayList<LeaveTransaction>();
        listAdapter = new LeaveAdapter(getActivity().getApplicationContext(), listData);

        listData.addAll(result);
        lView.setAdapter(listAdapter);
    }

    private class PopulateLeaveHistoryTask extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                setListAdapter(result);
            }
            else{
                noLeaveHistory.setText("No leave history to display");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return getLeaveHistory();

        }

        public List<LeaveTransaction> getLeaveHistory() {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/getAllLeavesAppliedByEmployee?employeeId={userId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, userId);
            LeaveTransaction[] leaveArray = response.getBody();
            List<LeaveTransaction> result = Arrays.asList(leaveArray);
            return result;
        }
    }


}
