package hrm.com.hrmprototype;

import android.app.Application;
import android.content.Context;

/**
 * Created by Beans on 5/8/2015.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}