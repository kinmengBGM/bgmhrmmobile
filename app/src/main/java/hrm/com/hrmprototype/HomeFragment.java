package hrm.com.hrmprototype;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import hrm.com.model.Employee;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Users;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements View.OnClickListener{

    private Employee employee;

    private String username;
    private String password;
    private Users user;
    private int userId;
    private String empUsername;

    private TextView uReason, uStatus, uDates;

    private TextView aEmployee, aLeaveType, aDates, aNoOfDays, aReason;

    private RelativeLayout upcomingLeaveLayout, approveLeaveLayout, approveNoDataLayout, upcomingNoDataLayout, upcomingGotDataLayout;
    private SwipeLayout approveGotDataLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Home");


        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.user = ((HomeActivity) getActivity()).getActiveUser();
        this.userId = user.getId();
        this.empUsername = user.getUsername();

        employee=((HomeActivity)getActivity()).getActiveEmployee();

        TextView employeeName = (TextView) rootView.findViewById(R.id.employeeName);
        TextView employeePosition = (TextView) rootView.findViewById(R.id.employeePosition);
        employeeName.setText(employee.getName());
        employeePosition.setText(employee.getPosition());

        uReason = (TextView) rootView.findViewById(R.id.upcomingLeaveReason);
        uStatus = (TextView) rootView.findViewById(R.id.upcomingLeaveStatus);
        uDates = (TextView) rootView.findViewById(R.id.upcomingLeaveDates);

        aEmployee = (TextView) rootView.findViewById(R.id.approveLeaveName);
        aReason = (TextView) rootView.findViewById(R.id.approveLeaveReason);
        aDates = (TextView) rootView.findViewById(R.id.approveLeaveDates);
        aLeaveType = (TextView) rootView.findViewById(R.id.approveLeaveType);
        aNoOfDays = (TextView) rootView.findViewById(R.id.approveLeaveNoOfDays);

        Button btnEditProfile = (Button) rootView.findViewById(R.id.btnHomeEditProfile);
        btnEditProfile.setOnClickListener(this);

        Button btnApplyLeave = (Button) rootView.findViewById(R.id.btnHomeApplyLeave);
        btnApplyLeave.setOnClickListener(this);

        Button btnUpcomingLeave = (Button) rootView.findViewById(R.id.btnViewAllUpcomingLeave);
        btnUpcomingLeave.setOnClickListener(this);

        Button btnApproveLeave = (Button) rootView.findViewById(R.id.btnViewAllApproveLeave);
        btnApproveLeave.setOnClickListener(this);

        approveLeaveLayout = (RelativeLayout) rootView.findViewById(R.id.layoutHomeApproveLeave);
        upcomingLeaveLayout = (RelativeLayout) rootView.findViewById(R.id.layoutHomeUpcomingLeave);
        approveNoDataLayout = (RelativeLayout) rootView.findViewById(R.id.layoutHomeApproveLeaveNoData);
        approveGotDataLayout = (SwipeLayout) rootView.findViewById(R.id.layoutHomeApproveLeaveGotData);
        upcomingNoDataLayout = (RelativeLayout) rootView.findViewById(R.id.layoutHomeUpcomingLeaveNoData);
        upcomingGotDataLayout = (RelativeLayout) rootView.findViewById(R.id.layoutHomeUpcomingLeaveGotData);

        updateUiValues();
        return rootView;
    }

    public void updateUiValues(){
        if(((HomeActivity)getActivity()).getHasUpcomingLeaveAccess()){

            upcomingLeaveLayout.setVisibility(View.VISIBLE);

            PopulateUpcomingLeaveTask populateUpcomingLeaveTask = new PopulateUpcomingLeaveTask();
            populateUpcomingLeaveTask.execute();
        }
        else
            upcomingLeaveLayout.setVisibility(View.GONE);

        if(((HomeActivity)getActivity()).getHasLeaveApprovalAccess()){

            approveLeaveLayout.setVisibility(View.VISIBLE);

            PopulateApproveLeaveTaskList populateApproveLeaveTaskList = new PopulateApproveLeaveTaskList();
            populateApproveLeaveTaskList.execute();
        }
        else
            approveLeaveLayout.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnHomeEditProfile:
                ((HomeActivity)getActivity()).switchFragment("profile");
                break;

            case R.id.btnHomeApplyLeave:
                ((HomeActivity)getActivity()).switchFragment("applyleave");
                break;

            case R.id.btnViewAllUpcomingLeave:
                ((HomeActivity)getActivity()).switchFragment("upcomingleave");
                break;

            case R.id.btnViewAllApproveLeave:
                ((HomeActivity)getActivity()).switchFragment("leaveapproval");
                break;
        }
    }

    private class PopulateUpcomingLeaveTask extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                upcomingNoDataLayout.setVisibility(View.GONE);
                upcomingGotDataLayout.setVisibility(View.VISIBLE);

                LeaveTransaction first = result.get(0);
                if (first.getStatus().equals("Approved")) uStatus.setTextColor(Color.parseColor("#00a208"));
                else if (first.getStatus().equals("Rejected")) uStatus.setTextColor(Color.parseColor("#b90000"));
                else if (first.getStatus().equals("Pending")) uStatus.setTextColor(Color.parseColor("#cf5810"));
                else if (first.getStatus().equals("Cancelled")) uStatus.setTextColor(Color.parseColor("#1066cf"));

                uReason.setText(first.getReason());
                uStatus.setText(first.getStatus());
                uDates.setText(first.fetchStartTimeStr() + " to " + first.fetchEndTimeStr());
            }
            else{
                upcomingNoDataLayout.setVisibility(View.VISIBLE);
                upcomingGotDataLayout.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return getUpcomingLeaves();

        }

        public List<LeaveTransaction> getUpcomingLeaves() {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/getAllFutureLeavesAppliedByEmployee?employeeId={userId}&todayDate={todayDate}";
            RestTemplate restTemplate = new RestTemplate();

            java.util.Date utilDate = new java.util.Date();
            Date todayDate = new Date(utilDate.getTime());

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<LeaveTransaction[]> response = restTemplate.exchange(url, HttpMethod.GET, request, LeaveTransaction[].class, userId, todayDate);
            LeaveTransaction[] leaveArray = response.getBody();
            List<LeaveTransaction> result = Arrays.asList(leaveArray);
            return result;
        }
    }

    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                approveNoDataLayout.setVisibility(View.GONE);
                approveGotDataLayout.setVisibility(View.VISIBLE);

                LeaveTransaction first = result.get(0);

                aEmployee.setText(first.getEmployee().getName());
                aLeaveType.setText(first.getLeaveType().getDescription());
                aNoOfDays.setText("Number of Days: " + first.getNumberOfDays().toString());
                aReason.setText("Reason: " + first.getReason());
                aDates.setText(first.fetchStartTimeStr() + " to " + first.fetchEndTimeStr());

            } else {
                approveNoDataLayout.setVisibility(View.VISIBLE);
                approveGotDataLayout.setVisibility(View.GONE);
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

