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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_base, container, false);

        ((HomeActivity)getActivity()).enableNavigationDrawer(true);

        Fragment viewProfile= ViewProfile.newInstance(new ProfileFragmentChangeListener() {

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            @Override
            public void onEditBasicInfo() {

                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editBasic = new EditBasicDetails();
                transaction.replace(R.id.child_fragment, editBasic).commit();
            }

            @Override
            public void onEditContactInfo() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editContact = new EditContactInfo();
                transaction.replace(R.id.child_fragment, editContact).commit();

            }

            @Override
            public void onEditAddressInfo() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editAddress = new EditAddress();
                transaction.replace(R.id.child_fragment, editAddress).commit();

            }

            @Override
            public void onCreateAddress() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment createAddress = new CreateAddress();
                transaction.replace(R.id.child_fragment, createAddress).commit();

            }

            @Override
            public void onEditWorkInfo() {
                ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Fragment editWork = new EditWorkInfo();
                transaction.replace(R.id.child_fragment, editWork).commit();

            }
        });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment, viewProfile).commit();
        return rootView;
    }


}
