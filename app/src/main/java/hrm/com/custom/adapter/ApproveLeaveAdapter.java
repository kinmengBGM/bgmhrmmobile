package hrm.com.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

import hrm.com.custom.listener.ApproveLeaveListener;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 4/7/2015.
 */
public class ApproveLeaveAdapter extends ArraySwipeAdapter<LeaveTransaction> implements View.OnClickListener {

    private Context context;
    private List<LeaveTransaction> approveLeaveList;
    private ApproveLeaveListener listener;

    private LeaveTransaction leaveTransaction;

    public ApproveLeaveAdapter(Context context, int resource, List<LeaveTransaction> approveLeaveList, ApproveLeaveListener listener) {
        super(context, resource, approveLeaveList);
        this.context = context;
        this.approveLeaveList=approveLeaveList;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //super.getView(position, convertView, parent);
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.approve_leave_row_layout, null);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
        leaveTransaction = approveLeaveList.get(position);

        TextView employeeName = (TextView) v.findViewById(R.id.txtApproveEmployeeName);
        TextView leaveType = (TextView) v.findViewById(R.id.txtApproveLeaveType);
        TextView dates = (TextView) v.findViewById(R.id.txtApproveDates);
        TextView numberOfDays = (TextView) v.findViewById(R.id.txtApproveNoOfDays);
        TextView reason = (TextView) v.findViewById(R.id.txtApproveReason);

        employeeName.setText(leaveTransaction.getEmployee().getName());
        leaveType.setText(leaveTransaction.getLeaveType().getDescription());
        numberOfDays.setText("Number of Days: " + leaveTransaction.getNumberOfDays().toString());
        reason.setText("Reason: " + leaveTransaction.getReason());
        dates.setText(dateFormat.format(leaveTransaction.getStartDateTime()) + " to " + dateFormat.format(leaveTransaction.getEndDateTime()));

        ImageView btnReject = (ImageView)v.findViewById(R.id.rejectView);
        btnReject.setOnClickListener(this);

        ImageView btnApprove = (ImageView)v.findViewById(R.id.approveView);
        btnApprove.setOnClickListener(this);

        return v;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

    public LeaveTransaction getItem(int position){
        if (approveLeaveList != null)
            return approveLeaveList.get(position);
        return null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rejectView:
                listener.onRejectSelected(leaveTransaction.getId());
                break;

            case R.id.approveView:
                listener.onApproveSelected();
                break;
        }
    }
}

