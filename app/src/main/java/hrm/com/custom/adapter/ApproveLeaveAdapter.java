package hrm.com.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.List;

import hrm.com.custom.listener.ApproveLeaveListener;
import hrm.com.custom.listener.ViewSickLeaveAttachmentListener;
import hrm.com.hrmprototype.R;
import hrm.com.model.LeaveTransaction;

/**
 * Created by Beans on 4/7/2015.
 */
public class ApproveLeaveAdapter extends ArraySwipeAdapter<LeaveTransaction>{

    private Context context;
    private List<LeaveTransaction> approveLeaveList;
    private ApproveLeaveListener listener;
    private ViewSickLeaveAttachmentListener sListener;


    public ApproveLeaveAdapter(Context context, int resource, List<LeaveTransaction> approveLeaveList, ApproveLeaveListener listener, ViewSickLeaveAttachmentListener sListener) {
        super(context, resource, approveLeaveList);
        this.context = context;
        this.approveLeaveList = approveLeaveList;
        this.listener = listener;
        this.sListener = sListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //super.getView(position, convertView, parent);
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_approve_leave, null);
        }

        //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
        final LeaveTransaction leaveTransaction = approveLeaveList.get(position);

        TextView employeeName = (TextView) v.findViewById(R.id.txtApproveEmployeeName);
        TextView leaveType = (TextView) v.findViewById(R.id.txtApproveLeaveType);
        TextView dates = (TextView) v.findViewById(R.id.txtApproveDates);
        TextView numberOfDays = (TextView) v.findViewById(R.id.txtApproveNoOfDays);
        TextView reason = (TextView) v.findViewById(R.id.txtApproveReason);

        employeeName.setText(leaveTransaction.getEmployee().getName());
        leaveType.setText(leaveTransaction.getLeaveType().getDescription());
        numberOfDays.setText("Number of Days: " + leaveTransaction.getNumberOfDays().toString());
        reason.setText("Reason: " + leaveTransaction.getReason());
        dates.setText(leaveTransaction.fetchStartTimeStr() + " to " + leaveTransaction.fetchEndTimeStr());

        final ImageView sickLeaveAttachment = (ImageView)v.findViewById(R.id.sickLeaveAttachment);
        if((leaveTransaction.getLeaveType().getDescription()).equals("Sick leave"))
            sickLeaveAttachment.setVisibility(View.VISIBLE);
        else
            sickLeaveAttachment.setVisibility(View.GONE);

        sickLeaveAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sListener.onViewAttachment(leaveTransaction);
            }
        });

        ImageView btnReject = (ImageView)v.findViewById(R.id.rejectView);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRejectSelected(leaveTransaction.getId());
            }
        });

        ImageView btnApprove = (ImageView)v.findViewById(R.id.approveView);
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onApproveSelected(leaveTransaction.getId());
            }
        });

        return v;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

/*
    public LeaveTransaction getItem(int position){
        if (approveLeaveList != null)
            return approveLeaveList.get(position);
        return null;
    }
*/

}

