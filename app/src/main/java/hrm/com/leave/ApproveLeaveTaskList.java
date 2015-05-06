package hrm.com.leave;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hrm.com.custom.adapter.ApproveLeaveAdapter;
import hrm.com.custom.fragment.RejectLeaveDialog;
import hrm.com.custom.fragment.SickLeaveDialog;
import hrm.com.custom.listener.ApproveLeaveListener;
import hrm.com.custom.listener.RejectLeaveListener;
import hrm.com.custom.listener.TaskListener;
import hrm.com.custom.listener.ViewSickLeaveAttachmentListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Users;
import hrm.com.webservice.LeaveApplicationFlowWS;
import hrm.com.webservice.LeaveApprovalManagement;

/**
 * Created by Beans on 4/14/2015.
 */

@SuppressLint("ValidFragment")
public class ApproveLeaveTaskList extends Fragment {
    private ApproveLeaveAdapter adpt;
    private List<LeaveTransaction> approveLeaveList = new ArrayList<LeaveTransaction>();

    private TextView noApproveLeave;

    private String username;
    private String password;
    private Users user;

    private ListView lView;

    int leaveTransactionId;
    private RejectLeaveDialog rej;

    private LeaveApplicationFlowWS leaveApplicationFlowWS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_approve_leave_list, container, false);

        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Leave Approval");

        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.user = ((HomeActivity) getActivity()).getActiveUser();

        lView = (ListView) rootView.findViewById(R.id.listViewAddress);
        noApproveLeave = (TextView) rootView.findViewById(R.id.noApproveLeave);

        leaveApplicationFlowWS = new LeaveApplicationFlowWS(username, password);

        PopulateApproveLeaveTaskList populate = new PopulateApproveLeaveTaskList();
        populate.execute();

        return rootView;
    }

    public void setApproveLeaveList(List<LeaveTransaction> leaveHistoryList) {
        this.approveLeaveList = leaveHistoryList;
    }

    public Users getUser() {
        return user;
    }

    public void setApproveLeaveList() {

        adpt = new ApproveLeaveAdapter(getActivity().getApplicationContext(), R.layout.row_approve_leave, approveLeaveList,

                new ApproveLeaveListener() {
                    @Override
                    public void onRejectSelected(int leaveId) {
                        leaveTransactionId = leaveId;
                        rej = new RejectLeaveDialog(new RejectLeaveListener() {
                            @Override
                            public void onRejectLeave(String reason) {
                                LeaveApprovalManagement leaveApprovalManagement =
                                        new LeaveApprovalManagement(username, password, leaveTransactionId, getUser());
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

                    @Override
                    public void onApproveSelected(int leaveId) {
                        leaveTransactionId = leaveId;
                        LeaveApprovalManagement leaveApprovalManagement =
                                new LeaveApprovalManagement(username, password, leaveTransactionId, getUser());
                        leaveApprovalManagement.doApproveLeaveRequest(new TaskListener() {
                            @Override
                            public void onTaskCompleted() {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.info_approveleave, Toast.LENGTH_SHORT).show();
                                PopulateApproveLeaveTaskList repopulate = new PopulateApproveLeaveTaskList();
                                repopulate.execute();
                            }
                        });
                    }
                },

                new ViewSickLeaveAttachmentListener() {
                    @Override
                    public void onViewAttachment(LeaveTransaction attachment) {
                        //TODO: Open pdf viewer
                        SickLeaveDialog sickLeaveDialog = new SickLeaveDialog(attachment.getSickLeaveAttachment());
                        sickLeaveDialog.show(getActivity().getSupportFragmentManager(), "AttachmentDialog");
                    }
                });
        lView.setAdapter(adpt);
    }

    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                lView.setVisibility(View.VISIBLE);
                noApproveLeave.setVisibility(View.GONE);

                setApproveLeaveList(result);
                setApproveLeaveList();
            } else {
                noApproveLeave.setVisibility(View.VISIBLE);
                lView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return leaveApplicationFlowWS.getPendingLeaveRequestsByRoleOfUser();

        }
    }

}

