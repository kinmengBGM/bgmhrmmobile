package hrm.com.custom.fragment;

/**
 * Created by Beans on 4/28/2015.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/22/2015.
 */
@SuppressLint("ValidFragment")
public class SickLeaveDialog extends DialogFragment{
    private ImageView img;
    private byte[] imageBytes;
    private ProgressBar progress;

    public SickLeaveDialog(byte[] imageBytes){ this.imageBytes = imageBytes;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sick_leave, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        progress = (ProgressBar)view.findViewById(R.id.progressBar);
        img = (ImageView) view.findViewById(R.id.imgSickLeave);
        SetImageBitmap setImageBitmap = new SetImageBitmap();
        setImageBitmap.execute();
        return view;
    }

    private int dpToPx(int dp)
    {
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    private class SetImageBitmap extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageBytes = null;

            int bwidth=bm.getWidth();
            int bheight=bm.getHeight();

            WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int bounding = dpToPx(size.x);
            float xScale = ((float) bounding) / bwidth;
            float yScale = ((float) bounding) / bheight;
            float scale = (xScale <= yScale) ? xScale : yScale;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            bm = Bitmap.createBitmap(bm, 0, 0, bwidth, bheight, matrix, true);

            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            if(bitmap != null)
                img.setImageBitmap(bitmap);

            if (img.getDrawable() == null) {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to display image", Toast.LENGTH_SHORT).show();
            }
            progress.setVisibility(View.GONE);
        }
    }
}
