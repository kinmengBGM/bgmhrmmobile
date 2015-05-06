package hrm.com.custom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import hrm.com.custom.utils.Fx;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 5/6/2015.
 */
public class UpcomingLeaveAdapter extends ArrayAdapter<LeaveTransaction> {
    private Context _context;
    private List<LeaveTransaction> listData;

    public UpcomingLeaveAdapter(Context context, List<LeaveTransaction> listData) {
        super(context, R.layout.item_row_year_leave, listData);
        this._context = context;
        this.listData = listData;
    }

    //Called everytime a row is created
    public View getView(int position, View convertView, ViewGroup parent) {

        LeaveTransaction child = (LeaveTransaction) getItem(position);
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
        else if (child.getStatus().equals("Rejected"))
            status.setTextColor(Color.parseColor("#b90000"));
        else if (child.getStatus().equals("Pending"))
            status.setTextColor(Color.parseColor("#cf5810"));
        else if (child.getStatus().equals("Cancelled"))
            status.setTextColor(Color.parseColor("#1066cf"));

        status.setText(child.getStatus());
        return convertView;

    }

    public int getCount() {
        if (listData != null)
            return listData.size();
        return 0;
    }

    public LeaveTransaction getItem(int position) {
        if (listData != null)
            return listData.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (listData != null)
            return listData.get(position).hashCode();
        return 0;
    }

    public List<LeaveTransaction> getItemList() {
        return listData;
    }

    public void setItemList(List<LeaveTransaction> addressList) {
        this.listData = listData;
    }

    public void toggle_contents(View v) {
        if (v.isShown()) {
            Fx.slide_up(_context, v);
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
            Fx.slide_down(_context, v);
        }
    }
}