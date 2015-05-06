package hrm.com.custom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import hrm.com.custom.utils.Fx;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 4/7/2015.
 */

public class LeaveByYearAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private HashMap<Integer, List<LeaveTransaction>> listData;
    private List<Integer> listDataHeader; // header titles

    public LeaveByYearAdapter(Context context, List<Integer> listDataHeader, HashMap<Integer, List<LeaveTransaction>> listData) {
        this._context = context;
        this.listDataHeader = listDataHeader;
        this.listData = listData;

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listData.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        LeaveTransaction child = (LeaveTransaction) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_row_year_leave, null);
        }

        TextView reason = (TextView) convertView.findViewById(R.id.reason);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView dates = (TextView) convertView.findViewById(R.id.dates);
        TextView leaveType = (TextView) convertView.findViewById(R.id.leaveType);
        TextView noOfDays = (TextView) convertView.findViewById(R.id.noOfDays);

        reason.setText(child.getReason());
        dates.setText(child.fetchStartTimeStr() + " to " + child.fetchEndTimeStr());
        leaveType.setText(child.getLeaveType().getDescription());
        noOfDays.setText("Number of days: " + String.valueOf(child.getNumberOfDays()));

        final LinearLayout expandableView = (LinearLayout) convertView.findViewById(R.id.expandableView);

        reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle_contents(expandableView);
            }
        });

        if (child.getStatus().equals("Approved")) status.setTextColor(Color.parseColor("#00a208"));
        else if (child.getStatus().equals("Rejected")) status.setTextColor(Color.parseColor("#b90000"));
        else if (child.getStatus().equals("Pending")) status.setTextColor(Color.parseColor("#cf5810"));
        else if (child.getStatus().equals("Cancelled")) status.setTextColor(Color.parseColor("#1066cf"));

        status.setText(child.getStatus());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listData.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        int header = (int) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_year_leave, null);
        }

        TextView year = (TextView) convertView.findViewById(R.id.year);
        year.setText(String.valueOf(header));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void toggle_contents(View v){
        if(v.isShown()){
            Fx.slide_up(_context, v);
            v.setVisibility(View.GONE);
        }
        else{
            v.setVisibility(View.VISIBLE);
            Fx.slide_down(_context, v);
        }
    }
}

