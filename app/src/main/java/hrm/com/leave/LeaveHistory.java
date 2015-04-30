package hrm.com.leave;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hrm.com.custom.adapter.LeaveAdapter;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;
import hrm.com.webservice.LeaveTransactionWS;

/**
 * Created by Beans on 4/7/2015.
 */
@SuppressLint("ValidFragment")
public class LeaveHistory extends Fragment{
    private ExpandableListAdapter listAdapter;
    private List<LeaveTransaction> listData;

    private ExpandableListView lView;
    private TextView noLeaveHistory;

    private int userId;

    private LeaveTransactionWS leaveTransactionWS;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Leave History");

        String username = ((HomeActivity) getActivity()).getUsername();
        String password = ((HomeActivity) getActivity()).getPassword();
        this.userId = ((HomeActivity) getActivity()).getActiveUser().getId();

        lView = (ExpandableListView) rootView.findViewById(R.id.listViewAddress);
        noLeaveHistory = (TextView) rootView.findViewById(R.id.noLeaveHistory);

        leaveTransactionWS = new LeaveTransactionWS(username, password);

        PopulateLeaveHistoryTask populateLeaveHistoryTask = new PopulateLeaveHistoryTask();
        populateLeaveHistoryTask.execute();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    public void setListAdapter(List<LeaveTransaction> result){
        listData = new ArrayList<LeaveTransaction>();
        listAdapter = new LeaveAdapter(getActivity().getApplicationContext(), listData);

        Collections.sort(result, new Comparator<LeaveTransaction>() {
            public int compare(LeaveTransaction o1, LeaveTransaction o2) {
                if (o1.getStartDateTime() == null || o2.getStartDateTime() == null)
                    return 0;
                return o1.getStartDateTime().compareTo(o2.getStartDateTime());
            }
        });

        listData.addAll(result);
        lView.setAdapter(listAdapter);
    }

    private class PopulateLeaveHistoryTask extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                noLeaveHistory.setVisibility(View.GONE);
                lView.setVisibility(View.VISIBLE);
                setListAdapter(result);
            }
            else{
                noLeaveHistory.setVisibility(View.VISIBLE);
                lView.setVisibility(View.GONE);
                noLeaveHistory.setText(R.string.no_leave_history);
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

            return leaveTransactionWS.getAllLeavesAppliedByEmployee(userId);
        }
    }


}
