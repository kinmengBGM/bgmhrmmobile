package hrm.com.hrmprototype;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Beans on 4/22/2015.
 */
public interface DrawerItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
    public String getTitle();
}
