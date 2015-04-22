
package hrm.com.leave;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import hrm.com.custom.adapter.ApproveLeaveAdapter;
import hrm.com.custom.fragment.RejectLeaveDialog;
import hrm.com.custom.listener.ApproveLeaveListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Users;
/**
 * Created by Beans on 4/14/2015.
 */

@SuppressLint("ValidFragment")
public class ApproveLeaveTaskList extends Fragment implements AdapterView.OnItemClickListener{
    private ApproveLeaveAdapter adpt;
    private List<LeaveTransaction> approveLeaveList = new ArrayList<LeaveTransaction>();

    private TextView noLeaveHistory;

    private String username;
    private String password;
    private Users user;

    private String empUsername;

    private List<String> approveLeaveListString;
    private ListView lView;

    public ApproveLeaveTaskList() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_approve_leave_list, container, false);

        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.user = ((HomeActivity) getActivity()).getActiveUser();
        empUsername = user.getUsername();

        lView = (ListView) rootView.findViewById(R.id.listViewAddress);
        noLeaveHistory = (TextView) rootView.findViewById(R.id.textView);
        approveLeaveListString = new ArrayList<String>();

        PopulateApproveLeaveTaskList populate = new PopulateApproveLeaveTaskList();
        populate.execute();

        lView.setOnItemClickListener(this);
        return rootView;
    }

    public void setApproveLeaveList(List<LeaveTransaction> leaveHistoryList){
        this.approveLeaveList = leaveHistoryList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*        ApproveLeaveDialog alertdFragment = new ApproveLeaveDialog(leaveHistoryList.get(position).getEmployee().getId(), leaveHistoryList.get(position).getLeaveType().getId());
        // Show Alert DialogFragment
        alertdFragment.show(getActivity().getSupportFragmentManager(), "Alert Dialog Fragment");*/
    }

    public void setApproveLeaveList(){
/*        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");;
        for(LeaveTransaction x : approveLeaveList) {
            approveLeaveListString.add(x.getEmployee().getName() + "\n"
                    + x.getLeaveType().getDescription() + "\n"
                    + dateFormat.format(x.getStartDateTime()) + " to " + dateFormat.format(x.getEndDateTime()) + "\n"
                    + x.getReason()
            );
        }*/

        adpt = new ApproveLeaveAdapter(getActivity().getApplicationContext(), R.layout.approve_leave_row_layout, approveLeaveList, new ApproveLeaveListener() {
            @Override
            public void onRejectSelected(int empId, int leaveTypeId) {
                Toast.makeText(getActivity().getApplicationContext(), "REJECT", Toast.LENGTH_SHORT).show();
                RejectLeaveDialog rej = new RejectLeaveDialog(empId, leaveTypeId);
                rej.show(getFragmentManager(), "Reject Leave");

            }

            @Override
            public void onApproveSelected() {
                Toast.makeText(getActivity().getApplicationContext(), "APPROVE", Toast.LENGTH_SHORT).show();

            }
        });
        lView.setAdapter(adpt);
    }

    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                setApproveLeaveList(result);
                setApproveLeaveList();
            }
            else{
                noLeaveHistory.setText("No leaves to be approved");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return getApproveLeavesTaskList();

        }

        public List<LeaveTransaction> getApproveLeavesTaskList() {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveApplicationFlow/getPendingLeaveRequestsByRoleOfUser?username={empUsername}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, empUsername);
            LeaveTransaction[] leaveArray = response.getBody();
            List<LeaveTransaction> result = Arrays.asList(leaveArray);
            return result;
        }
    }


}

