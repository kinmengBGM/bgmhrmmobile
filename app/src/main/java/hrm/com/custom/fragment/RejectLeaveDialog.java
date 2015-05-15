package hrm.com.custom.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import hrm.com.custom.listener.RejectLeaveListener;
import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/22/2015.
 */
@SuppressLint("ValidFragment")
public class RejectLeaveDialog extends DialogFragment implements View.OnClickListener {


    private EditText rejectReason;
    private RejectLeaveListener mListener;

    public RejectLeaveDialog(RejectLeaveListener mListener){
        this.mListener = mListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reject_leave, container);
        getDialog().setTitle("Reject leave application?");

        rejectReason = (EditText) view.findViewById(R.id.editRejectReason);
        Button cancel = (Button) view.findViewById(R.id.btnRejectLeaveCancel);
        cancel.setOnClickListener(this);

        Button reject = (Button) view.findViewById(R.id.btnRejectLeave);
        reject.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnRejectLeaveCancel:
                dismiss();
                break;
            case R.id.btnRejectLeave:
                if(!StringUtils.isBlank(rejectReason.getText()))
                    mListener.onRejectLeave(rejectReason.getText().toString());
                else
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_reason_empty, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
