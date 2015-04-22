package hrm.com.custom.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hrm.com.custom.adapter.DrawerItemCustomAdapter;
import hrm.com.hrmprototype.DrawerItem;
import hrm.com.hrmprototype.R;

/**
 * Created by Beans on 4/22/2015.
 */
public class Header implements DrawerItem {
    private final String         name;

    public Header(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return DrawerItemCustomAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.drawer_item_header, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.separator);
        text.setText(name);

        return view;
    }

    @Override
    public String getTitle() {
        return null;
    }

}