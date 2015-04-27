package hrm.com.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hrm.com.custom.listener.ProfileFragmentChangeListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileBaseFragment extends Fragment {


    public ProfileBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        ((HomeActivity)getActivity()).enableNavigationDrawer(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_home, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(true);

        Fragment viewProfile= ViewProfile.newInstance(new ProfileFragmentChangeListener() {

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            @Override
            public void onEditBasicInfo() {

                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editBasic = new EditBasicDetails();
                transaction.replace(R.id.content_frame, editBasic).commit();
            }

            @Override
            public void onEditContactInfo() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editContact = new EditContactInfo();
                transaction.replace(R.id.content_frame, editContact).commit();

            }

            @Override
            public void onEditAddressInfo() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editAddress = new EditAddress();
                transaction.replace(R.id.content_frame, editAddress).commit();

            }

            @Override
            public void onCreateAddress() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment createAddress = new CreateAddress();
                transaction.replace(R.id.content_frame, createAddress).commit();

            }

            @Override
            public void onEditWorkInfo() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editWork = new EditWorkInfo();
                transaction.replace(R.id.content_frame, editWork).commit();

            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, viewProfile).addToBackStack("Profile").commit();
        return rootView;
    }


}
