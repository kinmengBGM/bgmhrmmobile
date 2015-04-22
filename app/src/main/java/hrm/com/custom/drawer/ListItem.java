package hrm.com.custom.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import hrm.com.custom.adapter.DrawerItemCustomAdapter;
import hrm.com.hrmprototype.DrawerItem;
import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/22/2015.
 */
public class ListItem implements DrawerItem {
    private final int mIcon;
    private final String mString;

    public ListItem(int mIcon, String mString) {
        this.mIcon = mIcon;
        this.mString = mString;
    }

    @Override
    public int getViewType() {
        return DrawerItemCustomAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.drawer_item_row, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView text = (TextView) view.findViewById(R.id.title);
        icon.setImageResource(mIcon);
        text.setText(mString);

        return view;
    }

    @Override
    public String getTitle() {
        return mString;
    }

}