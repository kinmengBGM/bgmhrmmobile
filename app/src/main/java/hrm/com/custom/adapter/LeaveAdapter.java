package hrm.com.custom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 4/7/2015.
 */

public class LeaveAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<LeaveTransaction> listData;

    public LeaveAdapter(Context context, List<LeaveTransaction> listData) {
        this._context = context;
        this.listData = listData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        LeaveTransaction child= listData.get(groupPosition);
        return child;
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
            convertView = infalInflater.inflate(R.layout.item_row_leave_child, null);
        }

        TextView txtDates = (TextView) convertView
                .findViewById(R.id.txtLeaveDates);
        TextView txtLeaveType = (TextView) convertView
                .findViewById(R.id.txtLeaveType);
        TextView txtNoOfDays = (TextView) convertView
                .findViewById(R.id.txtNoOfDays);

        txtDates.setText(child.fetchStartTimeStr() + " to " + child.fetchEndTimeStr());
        txtLeaveType.setText(child.getLeaveType().getDescription());
        txtNoOfDays.setText(child.getNumberOfDays().toString() + " days");
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LeaveTransaction head = (LeaveTransaction) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leave_row_layout, null);
        }

        TextView reason = (TextView) convertView.findViewById(R.id.reason);
        TextView status = (TextView) convertView.findViewById(R.id.status);

        reason.setText(head.getReason());

        if (head.getStatus().equals("Approved")) status.setTextColor(Color.parseColor("#00a208"));
        else if (head.getStatus().equals("Rejected")) status.setTextColor(Color.parseColor("#b90000"));
        else if (head.getStatus().equals("Pending")) status.setTextColor(Color.parseColor("#cf5810"));
        else if (head.getStatus().equals("Cancelled")) status.setTextColor(Color.parseColor("#1066cf"));

        status.setText(head.getStatus());

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
}

