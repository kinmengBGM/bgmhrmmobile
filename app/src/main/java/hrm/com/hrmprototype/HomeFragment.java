package hrm.com.hrmprototype;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.List;

import hrm.com.custom.fragment.RejectLeaveDialog;
import hrm.com.custom.fragment.SickLeaveDialog;
import hrm.com.custom.listener.RejectLeaveListener;
import hrm.com.custom.listener.TaskListener;
import hrm.com.model.Employee;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Users;
import hrm.com.webservice.LeaveApplicationFlowWS;
import hrm.com.webservice.LeaveApprovalManagement;
import hrm.com.webservice.LeaveTransactionWS;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment implements View.OnClickListener{

    private Employee employee;
    private Users user;

    private String username, password;

    private int userId;

    private RejectLeaveDialog rej;

    private TextView uReason, uStatus, uDates;
    private TextView aEmployee, aLeaveType, aDates, aNoOfDays, aReason;
    private ImageView sickLeaveAttachment, rejectView, approveView;

    private RelativeLayout upcomingLeaveLayout, approveLeaveLayout, approveNoDataLayout, upcomingNoDataLayout, upcomingGotDataLayout;
    private SwipeLayout approveGotDataLayout;

    private LeaveTransactionWS leaveTransactionWS;
    private LeaveApplicationFlowWS leaveApplicationFlowWS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Home");

        username = ((HomeActivity) getActivity()).getUsername();
        password = ((HomeActivity) getActivity()).getPassword();
        this.userId = ((HomeActivity) getActivity()).getActiveUser().getId();
        this.user = ((HomeActivity) getActivity()).getActiveUser();
        this.employee=((HomeActivity)getActivity()).getActiveEmployee();

        leaveTransactionWS = new LeaveTransactionWS(username, password);
        leaveApplicationFlowWS = new LeaveApplicationFlowWS(username, password);


        initUiValues(rootView);

        return rootView;
    }

    public void initUiValues(View rootView){
        sickLeaveAttachment = (ImageView)rootView.findViewById(R.id.sickLeaveAttachment);
        rejectView = (ImageView)rootView.findViewById(R.id.rejectView);
        approveView = (ImageView)rootView.findViewById(R.id.approveView);

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


        if(((HomeActivity)getActivity()).getHasApplyLeaveAccess())
            btnApplyLeave.setVisibility(View.VISIBLE);
        else
            btnApplyLeave.setVisibility(View.GONE);


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

    public void updateUpcomingLeaveUI(List<LeaveTransaction> result){

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

    private class PopulateUpcomingLeaveTask extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);
            updateUpcomingLeaveUI(result);
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return leaveTransactionWS.getAllFutureLeavesAppliedByEmployee(userId);
        }
    }

    public void updateApproveLeaveUI(List<LeaveTransaction> result){

        if (result.size() > 0) {
            approveNoDataLayout.setVisibility(View.GONE);
            approveGotDataLayout.setVisibility(View.VISIBLE);

            final LeaveTransaction first = result.get(0);

            if((first.getLeaveType().getDescription()).equals("Sick leave"))
                sickLeaveAttachment.setVisibility(View.VISIBLE);
            else
                sickLeaveAttachment.setVisibility(View.GONE);

            //Sick leave button
            sickLeaveAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SickLeaveDialog sickLeaveDialog = new SickLeaveDialog(first.getSickLeaveAttachment());
                    sickLeaveDialog.show(getActivity().getSupportFragmentManager(), "AttachmentDialog");
                }
            });

            //Reject button
            rejectView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    rej = new RejectLeaveDialog(new RejectLeaveListener() {
                        @Override
                        public void onRejectLeave(String reason) {
                            LeaveApprovalManagement leaveApprovalManagement =
                                    new LeaveApprovalManagement(username, password, first.getId(), user);
                            leaveApprovalManagement.doRejectLeaveRequest(reason, new TaskListener() {
                                @Override
                                public void onTaskCompleted() {
                                    rej.dismiss();
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.info_rejectleave, Toast.LENGTH_SHORT).show();
                                    PopulateApproveLeaveTaskList repopulate = new PopulateApproveLeaveTaskList();
                                    repopulate.execute();
                                }
                            });
                        }
                    });
                    rej.show(getFragmentManager(), "Reject Leave");
                }
            });

            approveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LeaveApprovalManagement leaveApprovalManagement =
                            new LeaveApprovalManagement(username, password, first.getId(), user);
                    leaveApprovalManagement.doApproveLeaveRequest(new TaskListener() {
                        @Override
                        public void onTaskCompleted() {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.info_approveleave, Toast.LENGTH_SHORT).show();
                            PopulateApproveLeaveTaskList repopulate = new PopulateApproveLeaveTaskList();
                            repopulate.execute();
                        }
                    });
                }
            });

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

    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);
            updateApproveLeaveUI(result);
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
            return leaveApplicationFlowWS.getPendingLeaveRequestsByRoleOfUser();
        }
    }
}

