package hrm.com.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import hrm.com.hrmprototype.R;
import hrm.com.model.Address;

/**
 * Created by Beans on 3/31/2015.
 */
public class AddressAdapter extends ArrayAdapter<Address> {
    List<Address> addressList;
    Context context;

    public AddressAdapter(List<Address> addressList, Context ctx) {
        super(ctx, R.layout.row_address, addressList);
        this.addressList = addressList;
        this.context = ctx;
    }

    //Called everytime a row is created
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_address, null);
        }
        Address p = addressList.get(position);
        TextView addTypeView = (TextView) v.findViewById(R.id.addressType);
        TextView addLine1View = (TextView) v.findViewById(R.id.addressLine1);
        TextView addCountryView = (TextView) v.findViewById(R.id.country);

        addTypeView.setText(p.getAddressType());
        addLine1View.setText(p.getLine1());
        addCountryView.setText(p.getCountry());
        return v;

    }

    public int getCount() {
        if (addressList != null)
            return addressList.size();
        return 0;
    }

    public Address getItem(int position) {
        if (addressList != null)
            return addressList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (addressList != null)
            return addressList.get(position).hashCode();
        return 0;
    }

    public List<Address> getItemList() {
        return addressList;
    }

    public void setItemList(List<Address> addressList) {
        this.addressList = addressList;
    }

}
