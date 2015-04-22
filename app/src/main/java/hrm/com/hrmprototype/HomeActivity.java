package hrm.com.hrmprototype;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hrm.com.custom.adapter.DrawerItemCustomAdapter;
import hrm.com.custom.drawer.Header;
import hrm.com.custom.drawer.ListItem;
import hrm.com.leave.ApplyLeave;
import hrm.com.leave.LeaveHistory;
import hrm.com.leave.UpcomingLeave;
import hrm.com.leave.ApproveLeaveTaskList;
import hrm.com.model.Address;
import hrm.com.model.Employee;
import hrm.com.model.Users;
import hrm.com.profile.ProfileBaseFragment;


public class HomeActivity extends ActionBarActivity{

    //User info
    private int userId;
    private int addressId;
    private String username;
    private String password;
    private Employee activeEmployee;
    private Users activeUser;
    private List<Address> existingAddressList;

    private String updateAddressTag, createAddressTag, viewProfileTag;

    // Declare Variables
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter mMenuAdapter;
    List<DrawerItem> drawerItems;
    Fragment fragment1 = new Fragment1();
    Fragment profile = new ProfileBaseFragment();
    Fragment applyLeave = new ApplyLeave();
    Fragment leaveHistory = new LeaveHistory();
    Fragment leaveApproval = new ApproveLeaveTaskList();
    Fragment upcomingLeave = new UpcomingLeave();
    //Fragment leaveManagement = new LeaveManagementBaseFragment();
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        this.userId = intent.getIntExtra("userId", 0);
        this.username = intent.getStringExtra("username");
        this.password = intent.getStringExtra("password");
        this.activeEmployee = (Employee) intent.getSerializableExtra("employee");
        this.activeUser = (Users) intent.getSerializableExtra("user");

        //Don't show title when drawer is opened
        //mTitle = mDrawerTitle = getTitle();
      /*  title = new String[] { "Home", "My Profile", "Apply Leave", "Leave History", "Upcoming Leaves", "Leave Approval"};
        icon = new int[] {R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon_work, R.drawable.icon_admin, R.drawable.icon_admin, R.drawable.icon_admin};
*/
        drawerItems = new ArrayList<DrawerItem>();
        drawerItems.add(new ListItem(R.drawable.icon_home, "Home"));
        drawerItems.add(new ListItem(R.drawable.icon_profile, "My Profile"));
        drawerItems.add(new Header("Leave Management"));
        drawerItems.add(new ListItem(R.drawable.icon_work, "Apply Leave"));
        drawerItems.add(new ListItem(R.drawable.icon_admin, "Leave History"));
        drawerItems.add(new ListItem(R.drawable.icon_admin, "Upcoming Leaves"));
        drawerItems.add(new ListItem(R.drawable.icon_admin, "Leave Approval"));

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
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null) {
            selectItem(0);
        }
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
            if(position != 2) //header unclickable
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

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        switch (position) {
            case 0:
                ft.replace(R.id.content_frame, fragment1);
                break;
            case 1:
                ft.replace(R.id.content_frame, profile).addToBackStack("Profile");
                break;
            case 2:
                break;
            case 3:
                ft.replace(R.id.content_frame, applyLeave);
                break;
            case 4:
                ft.replace(R.id.content_frame, leaveHistory);
                break;
            case 5:
                ft.replace(R.id.content_frame, upcomingLeave);
                break;
            case 6:
                ft.replace(R.id.content_frame, leaveApproval);
                break;
        }
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

private boolean onBackPressed(FragmentManager fm) {
        if (fm != null) {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                invalidateOptionsMenu();
                return true;
            }

            List<Fragment> fragList = fm.getFragments();
            if (fragList != null && fragList.size() > 0) {
                for (Fragment frag : fragList) {
                    if (frag == null) {
                        continue;
                    }
                    if (frag.isVisible()) {
                        if (onBackPressed(frag.getChildFragmentManager())) {
                            invalidateOptionsMenu();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (onBackPressed(fm)) {
            return;
        }
        super.onBackPressed();
    }



    public int getAddressId(){return addressId;}
    public void setAddressId(int addressId) {this.addressId = addressId;}

    public List<Address> getExistingAddressList(){return existingAddressList;}
    public void setExistingAddressList(List<Address> existingAddressList){this.existingAddressList = existingAddressList;}

    public Users getActiveUser(){return activeUser;}
    public void setActiveUser(Users activeUser) {this.activeUser = activeUser;}

    public String getUsername(){ return username;}
    public void setUsername(String username){this.username = username;}

    public String getPassword(){return password;}
    public void setPassword(String password){ this.password = password;}

    public Employee getActiveEmployee(){ return activeEmployee; }
    public void setActiveEmployee(Employee activeEmployee){ this.activeEmployee = activeEmployee;}
}
