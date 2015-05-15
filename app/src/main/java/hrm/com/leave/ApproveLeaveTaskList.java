package hrm.com.leave;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private static final int REQUEST_VIEW_PDF = 78;

    private ApproveLeaveAdapter adpt;
    private List<LeaveTransaction> approveLeaveList = new ArrayList<LeaveTransaction>();

    private TextView noApproveLeave;
    private ListView lView;

    private String username;
    private String password;
    private Users user;

    private String pdfFilePath;

    private int leaveTransactionId;
    private RejectLeaveDialog rej;

    private LeaveApplicationFlowWS leaveApplicationFlowWS;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_approve_leave_list, container, false);

        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Leave Approval");
        setHasOptionsMenu(true);

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

    public boolean isPdf(String sickLeaveAttachmentName) {
        String name = sickLeaveAttachmentName.substring(sickLeaveAttachmentName.length() - 3);
        if (name.equals("pdf")) {
            return true;
        } else
            return false;
    }

    private File createPdfFile(String sickLeaveAttachmentName, byte[] bytes) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = sickLeaveAttachmentName + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".pdf",         /* suffix */
                storageDir      /* directory */
        );
        BufferedOutputStream bos = null;
        FileOutputStream os = null;
        pdfFilePath = pdfFile.getAbsolutePath();
        try {
            os = new FileOutputStream(pdfFile);
            bos = new BufferedOutputStream(os);
            bos.write(bytes);
        } finally {
            if (bos != null) {
                try {
                    //flush and close the BufferedOutputStream
                    bos.flush();
                    bos.close();
                    os.flush();
                    os.close();
                } catch (Exception e) {
                }
            }
        }
        return pdfFile;
    }

    public void setApproveLeaveList() {

        adpt = new ApproveLeaveAdapter(getActivity().getApplicationContext(), R.layout.row_approve_leave, approveLeaveList,
                new ApproveLeaveListener() {

                    //Select reject option
                    @Override
                    public void onRejectSelected(int leaveId) {
                        final ProgressDialog dialog = new ProgressDialog(getActivity());
                        leaveTransactionId = leaveId;
                        rej = new RejectLeaveDialog(new RejectLeaveListener() {

                            //Confirm reject
                            @Override
                            public void onRejectLeave(String reason) {
                                dialog.setMessage("Rejecting leave application...");
                                dialog.setCancelable(false);
                                dialog.show();
                                LeaveApprovalManagement leaveApprovalManagement =
                                        new LeaveApprovalManagement(username, password, leaveTransactionId, getUser());
                                leaveApprovalManagement.doRejectLeaveRequest(reason, new TaskListener() {
                                    @Override
                                    public void onTaskCompleted() {
                                        rej.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(getActivity().getApplicationContext(), R.string.info_rejectleave, Toast.LENGTH_SHORT).show();
                                        PopulateApproveLeaveTaskList repopulate = new PopulateApproveLeaveTaskList();
                                        repopulate.execute();
                                    }

                                    @Override
                                    public void onTaskNotCompleted() {
                                        rej.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        rej.show(getFragmentManager(), "Reject Leave");
                    }

                    @Override
                    public void onApproveSelected(int leaveId) {
                        leaveTransactionId = leaveId;
                        final ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Approving leave application...");
                        dialog.setCancelable(false);
                        dialog.show();
                        LeaveApprovalManagement leaveApprovalManagement =
                                new LeaveApprovalManagement(username, password, leaveTransactionId, getUser());
                        leaveApprovalManagement.doApproveLeaveRequest(new TaskListener() {
                            @Override
                            public void onTaskCompleted() {
                                dialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), R.string.info_approveleave, Toast.LENGTH_SHORT).show();
                                PopulateApproveLeaveTaskList repopulate = new PopulateApproveLeaveTaskList();
                                repopulate.execute();
                            }

                            @Override
                            public void onTaskNotCompleted() {
                                dialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },

                new ViewSickLeaveAttachmentListener() {
                    @Override
                    public void onViewAttachment(LeaveTransaction attachment) {
                        if (isPdf(attachment.getSickLeaveAttachmentName())) {
                            try {
                                File pdfFile = createPdfFile(attachment.getSickLeaveAttachmentName(), attachment.getSickLeaveAttachment());
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivityForResult(Intent.createChooser(intent, "View PDF File"), REQUEST_VIEW_PDF);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_pdf_viewer, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.error_sickleaveattachment_filecreation, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            SickLeaveDialog sickLeaveDialog = new SickLeaveDialog(attachment.getSickLeaveAttachment());
                            sickLeaveDialog.show(getActivity().getSupportFragmentManager(), "AttachmentDialog");
                        }
                    }
                });
        lView.setAdapter(adpt);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIEW_PDF) {
            File pdf = new File(pdfFilePath);
            pdf.delete();
        }
    }

    private class PopulateApproveLeaveTaskList extends AsyncTask<String, Void, List<LeaveTransaction>> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if (result == null){
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_timeout, Toast.LENGTH_SHORT).show();
            }
            else if(result.size() > 0) {
                lView.setVisibility(View.VISIBLE);
                noApproveLeave.setVisibility(View.GONE);

                setApproveLeaveList(result);
                setApproveLeaveList();
            } else {
                noApproveLeave.setVisibility(View.VISIBLE);
                lView.setVisibility(View.GONE);
            }
            dialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading leaves to be approved...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            try {
                return leaveApplicationFlowWS.getPendingLeaveRequestsByRoleOfUser();
            }catch(Exception e){
                return null;
            }
        }
    }

}

