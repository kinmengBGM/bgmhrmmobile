package hrm.com.hrmprototype;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hrm.com.model.Employee;
import hrm.com.profile.ProfileBaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment implements View.OnClickListener{

    Employee employee;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(false);
        inflater.inflate(R.menu.menu_home, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment1, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Home");
        employee=((HomeActivity)getActivity()).getActiveEmployee();

        TextView employeeName = (TextView) rootView.findViewById(R.id.employeeName);
        TextView employeePosition = (TextView) rootView.findViewById(R.id.employeePosition);
        employeeName.setText(employee.getName());
        employeePosition.setText(employee.getPosition());

        Button btnEditProfile = (Button) rootView.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnEditProfile:
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new ProfileBaseFragment()).addToBackStack("Profile").commit();
            break;
        }
    }
}

