package hrm.com.leave;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import hrm.com.custom.adapter.LeaveByYearAdapter;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;
import hrm.com.webservice.LeaveTransactionWS;

/**
 * Created by Beans on 4/7/2015.
 */
@SuppressLint("ValidFragment")
public class LeaveHistory extends Fragment {
    private ExpandableListView lView;
    private TextView noLeave;

    private int userId;

    private LeaveTransactionWS leaveTransactionWS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_leave, container, false);

        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Leave History");

        String username = ((HomeActivity) getActivity()).getUsername();
        String password = ((HomeActivity) getActivity()).getPassword();
        this.userId = ((HomeActivity) getActivity()).getActiveUser().getId();

        lView = (ExpandableListView) rootView.findViewById(R.id.expandableList);
        noLeave = (TextView) rootView.findViewById(R.id.noLeave);

        leaveTransactionWS = new LeaveTransactionWS(username, password);

        PopulateLeaveHistoryTask populateLeaveHistoryTask = new PopulateLeaveHistoryTask();
        populateLeaveHistoryTask.execute();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    public void sortByYear(List<LeaveTransaction> result) throws ParseException {

        HashMap<Integer, List<LeaveTransaction>> map = new HashMap<Integer, List<LeaveTransaction>>(); //list data

        for (LeaveTransaction leaveTransaction : result) {
            String startDate = leaveTransaction.fetchStartTimeStr();
            int year;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date parse = sdf.parse(startDate);
            Calendar c = Calendar.getInstance();
            c.setTime(parse);
            year = c.get(Calendar.YEAR);

            if (map.get(year) == null) {
                map.put(year, new ArrayList<LeaveTransaction>());
            }
            map.get(year).add(leaveTransaction);
        }

        List<Integer> listDataHeader = new ArrayList<Integer>(); // header titles

        for (int key : map.keySet()) {
            listDataHeader.add(key);
        }

        LeaveByYearAdapter adp = new LeaveByYearAdapter(getActivity().getBaseContext(), listDataHeader, map);
        lView.setAdapter(adp);
    }

    private class PopulateLeaveHistoryTask extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if(result.size() > 0) {
                noLeave.setVisibility(View.GONE);
                lView.setVisibility(View.VISIBLE);
                try {
                    sortByYear(result);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else{
                noLeave.setVisibility(View.VISIBLE);
                lView.setVisibility(View.GONE);
                noLeave.setText(R.string.no_leave_history);
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
