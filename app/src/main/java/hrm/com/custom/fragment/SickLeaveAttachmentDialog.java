package hrm.com.custom.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import hrm.com.custom.listener.SickLeaveAttachmentListener;
import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/23/2015.
 */
@SuppressLint("ValidFragment")
public class SickLeaveAttachmentDialog extends DialogFragment {

    SickLeaveAttachmentListener mListener;

    public SickLeaveAttachmentDialog(SickLeaveAttachmentListener mListener){
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select an option")
                .setItems(R.array.sickLeaveAttachmentOptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                mListener.onCameraSelected();
                                break;
                            case 1:
                                mListener.onGallerySelected();
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
