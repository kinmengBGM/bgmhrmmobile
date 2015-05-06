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

import java.util.ArrayList;
import java.util.List;

import hrm.com.custom.adapter.UpcomingLeaveAdapter;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;
import hrm.com.webservice.LeaveTransactionWS;

/**
 * Created by Beans on 4/7/2015.
 */
@SuppressLint("ValidFragment")
public class UpcomingLeave extends Fragment {

    private UpcomingLeaveAdapter listAdapter;
    private List<LeaveTransaction> listData;

    private ListView lView;
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
        View rootView = inflater.inflate(R.layout.fragment_upcoming_leave, container, false);


        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Upcoming Leave");

        String username = ((HomeActivity) getActivity()).getUsername();
        String password = ((HomeActivity) getActivity()).getPassword();
        this.userId = ((HomeActivity) getActivity()).getActiveUser().getId();

        lView = (ListView) rootView.findViewById(R.id.listView);
        noLeave = (TextView) rootView.findViewById(R.id.noLeave);

        leaveTransactionWS = new LeaveTransactionWS(username, password);

        PopulateUpcomingLeaveTask populateUpcomingLeaveTask = new PopulateUpcomingLeaveTask();
        populateUpcomingLeaveTask.execute();

        return rootView;
    }

    public void setListAdapter(List<LeaveTransaction> result) {
        listData = new ArrayList<LeaveTransaction>();
        listAdapter = new UpcomingLeaveAdapter(getActivity().getApplicationContext(), listData);

        listData.addAll(result);
        lView.setAdapter(listAdapter);
    }

    private class PopulateUpcomingLeaveTask extends AsyncTask<String, Void, List<LeaveTransaction>> {

        @Override
        protected void onPostExecute(List<LeaveTransaction> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                noLeave.setVisibility(View.GONE);
                lView.setVisibility(View.VISIBLE);
                setListAdapter(result);
            } else {
                noLeave.setVisibility(View.VISIBLE);
                lView.setVisibility(View.GONE);
                noLeave.setText(R.string.no_upcoming_leave);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LeaveTransaction> doInBackground(String... params) {
            return leaveTransactionWS.getAllFutureLeavesAppliedByEmployee(userId);
        }

    }


}
