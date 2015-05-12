package hrm.com.custom.fragment;

/**
 * Created by Beans on 4/28/2015.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/22/2015.
 */
@SuppressLint("ValidFragment")
public class SickLeaveDialog extends DialogFragment{

    byte[] imageBytes;
    public SickLeaveDialog(byte[] imageBytes){ this.imageBytes = imageBytes;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sick_leave, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ImageView img = (ImageView)view.findViewById(R.id.imgSickLeave);

        Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageBytes = null;

        /*
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        */
        img.setImageBitmap(bm);

        return view;
    }



}
