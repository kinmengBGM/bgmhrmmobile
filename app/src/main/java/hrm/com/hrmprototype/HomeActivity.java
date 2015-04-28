package hrm.com.hrmprototype;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import hrm.com.custom.adapter.DrawerItemCustomAdapter;
import hrm.com.custom.drawer.Header;
import hrm.com.custom.drawer.ListItem;
import hrm.com.custom.enums.Access;
import hrm.com.custom.listener.ProfileFragmentChangeListener;
import hrm.com.custom.rest.BasicAuthInterceptor;
import hrm.com.leave.ApplyLeave;
import hrm.com.leave.ApproveLeaveTaskList;
import hrm.com.leave.LeaveHistory;
import hrm.com.leave.UpcomingLeave;
import hrm.com.model.Employee;
import hrm.com.model.Users;
import hrm.com.profile.CreateAddress;
import hrm.com.profile.EditAddress;
import hrm.com.profile.EditBasicDetails;
import hrm.com.profile.EditContactInfo;
import hrm.com.profile.EditWorkInfo;
import hrm.com.profile.ViewProfile;

public class HomeActivity extends ActionBarActivity {

    //User info
    private int addressId;
    private String username;
    private String password;
    private Employee activeEmployee;
    private Users activeUser;
    //private List<Address> existingAddressList;
    private List<String> userRoles;

    // Declare Variables
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter mMenuAdapter;
    List<DrawerItem> drawerItems;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private Fragment currentFrag = null;
    private Stack<Fragment> fragmentStack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentStack = new Stack<Fragment>();

        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        this.username = intent.getStringExtra("username");
        this.password = intent.getStringExtra("password");
        this.activeEmployee = (Employee) intent.getSerializableExtra("employee");
        this.activeUser = (Users) intent.getSerializableExtra("user");

        GetUserRole getRoles = new GetUserRole();
        getRoles.execute();

        //Don't show title when drawer is opened
        //mTitle = mDrawerTitle = getTitle();
        /*title = new String[] { "Home", "My Profile", "Apply Leave", "Leave History", "Upcoming Leaves", "Leave Approval"};
        icon = new int[] {R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon_work, R.drawable.icon_admin, R.drawable.icon_admin, R.drawable.icon_admin};
*/

        drawerItems = new ArrayList<DrawerItem>();

        // Locate DrawerLayout in drawer_main.xml
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.listview_drawer);

        // Pass string arrays to MenuListAdapter
        mMenuAdapter = new DrawerItemCustomAdapter(HomeActivity.this, drawerItems);

        // Set the MenuListAdapter to the ListView
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setBackgroundResource(R.drawable.drawer_background);
        // Capture listview menu item click
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.icon_drawer, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                // Set the title on the action when drawer open
                getSupportActionBar().setTitle(mDrawerTitle);
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                selectItem(position);
        }
    }

    public void enableNavigationDrawer(boolean isEnabled) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isEnabled);
        getSupportActionBar().setHomeButtonEnabled(isEnabled);
        if(isEnabled){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void selectItem(int position) {
        DrawerItem drawerItem = mMenuAdapter.getItem(position);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if(drawerItem.getViewType() == mMenuAdapter.getItemViewType(0)){ //if drawerItem is ListItem

            if(currentFrag!=null)
                ft.remove(currentFrag);

            switch (drawerItem.getTitle()) {
                case "Home":
                    currentFrag= new Fragment1();
                    break;
                case "My Profile":
                    currentFrag= ViewProfile.newInstance(new ProfileFragmentChangeListener() {
                        @Override
                        public void onEditBasicInfo() {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            Fragment editBasic = new EditBasicDetails();
                            transaction.add(R.id.content_frame, editBasic);
                            fragmentStack.lastElement().onPause();
                            transaction.hide(fragmentStack.lastElement());
                            fragmentStack.push(editBasic);
                            transaction.commit();

                        }

                        @Override
                        public void onEditContactInfo() {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            Fragment editContact = new EditContactInfo();
                            transaction.add(R.id.content_frame, editContact);
                            fragmentStack.lastElement().onPause();
                            transaction.hide(fragmentStack.lastElement());
                            fragmentStack.push(editContact);
                            transaction.commit();
                        }

                        @Override
                        public void onEditAddressInfo() {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            Fragment editAddress = new EditAddress();
                            transaction.add(R.id.content_frame, editAddress);
                            fragmentStack.lastElement().onPause();
                            transaction.hide(fragmentStack.lastElement());
                            fragmentStack.push(editAddress);
                            transaction.commit();
                        }

                        @Override
                        public void onCreateAddress() {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            Fragment createAddress = new CreateAddress();
                            transaction.add(R.id.content_frame, createAddress);
                            fragmentStack.lastElement().onPause();
                            transaction.hide(fragmentStack.lastElement());
                            fragmentStack.push(createAddress);
                            transaction.commit();
                        }

                        @Override
                        public void onEditWorkInfo() {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            Fragment editWork = new EditWorkInfo();
                            transaction.add(R.id.content_frame, editWork);
                            fragmentStack.lastElement().onPause();
                            transaction.hide(fragmentStack.lastElement());
                            fragmentStack.push(editWork);
                            transaction.commit();
                        }

                    });
                    fragmentStack.push(currentFrag);
                    break;
                case "Apply Leave":
                    currentFrag= new ApplyLeave();
                    break;
                case "Leave History":
                    currentFrag= new LeaveHistory();
                    break;
                case "Upcoming Leaves":
                    currentFrag= new UpcomingLeave();
                    break;
                case "Leave Approval":
                    currentFrag=new ApproveLeaveTaskList();
                    break;
            }
        }
        ft.replace(R.id.content_frame, currentFrag);
        ft.commit();
        mDrawerList.setItemChecked(position, true);
        // Get the title followed by the position
        setTitle(drawerItems.get(position).getTitle());
        // Close drawer
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {

        if (fragmentStack.size() == 2) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragmentStack.lastElement().onDestroy();
            ft.remove(fragmentStack.pop());
            fragmentStack.lastElement().onResume();
            ft.show(fragmentStack.lastElement());
            ft.commit();
        } else {
            super.onBackPressed();
        }
    }

    private class GetUserRole extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPostExecute(List<String> result) {
            userRoles = result;

            boolean hasHomeAccess = false, hasProfileAccess = false, hasApplyLeaveAccess = false, hasLeaveHistoryAccess= false, hasUpcomingLeaveAccess = false, hasLeaveApprovalAccess = false;
            for(String x:userRoles){
                if(Access.HOME.hasAccess(x))
                   hasHomeAccess = true;
                if(Access.PROFILE.hasAccess(x))
                    hasProfileAccess = true;
                if(Access.APPLYLEAVE.hasAccess(x))
                    hasApplyLeaveAccess = true;
                if(Access.LEAVEHISTORY.hasAccess(x))
                    hasLeaveHistoryAccess = true;
                if(Access.UPCOMINGLEAVE.hasAccess(x))
                    hasUpcomingLeaveAccess = true;
                if(Access.LEAVEAPPROVAL.hasAccess(x))
                    hasLeaveApprovalAccess = true;
            }
            if(hasHomeAccess)
                drawerItems.add(new ListItem(R.drawable.icon_home, Access.HOME.toString()));
            if(hasProfileAccess)
                drawerItems.add(new ListItem(R.drawable.icon_profile, Access.PROFILE.toString()));
            drawerItems.add(new Header("Leave Management"));
            if(hasApplyLeaveAccess)
                drawerItems.add(new ListItem(R.drawable.icon_work, Access.APPLYLEAVE.toString()));
            if(hasLeaveHistoryAccess)
                drawerItems.add(new ListItem(R.drawable.icon_work, Access.LEAVEHISTORY.toString()));
            if(hasUpcomingLeaveAccess)
                drawerItems.add(new ListItem(R.drawable.icon_work, Access.UPCOMINGLEAVE.toString()));
            if(hasLeaveApprovalAccess)
                drawerItems.add(new ListItem(R.drawable.icon_work, Access.LEAVEAPPROVAL.toString()));

            selectItem(0);
            mMenuAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            userRoles = new ArrayList<String>();
        }

        @Override
        protected List<String> doInBackground(String... params) {

            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/role/findRoleNamesByUsername?username={username}";
            RestTemplate restTemplate = new RestTemplate();


            final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
            interceptors.add( new BasicAuthInterceptor( username, password ) );
            restTemplate.setInterceptors(interceptors);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            String[] roleArray = restTemplate.getForObject(url, String[].class, username);

            List<String> result = Arrays.asList(roleArray);
            return result;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public int getAddressId(){return addressId;}
    public void setAddressId(int addressId) {this.addressId = addressId;}

    /*public List<Address> getExistingAddressList(){return existingAddressList;}
    public void setExistingAddressList(List<Address> existingAddressList){this.existingAddressList = existingAddressList;}*/

    public Users getActiveUser(){return activeUser;}
    public void setActiveUser(Users activeUser) {this.activeUser = activeUser;}

    public String getUsername(){ return username;}
    public void setUsername(String username){this.username = username;}

    public String getPassword(){return password;}
    public void setPassword(String password){ this.password = password;}

    public Employee getActiveEmployee(){ return activeEmployee; }
    public void setActiveEmployee(Employee activeEmployee){ this.activeEmployee = activeEmployee;}
}
