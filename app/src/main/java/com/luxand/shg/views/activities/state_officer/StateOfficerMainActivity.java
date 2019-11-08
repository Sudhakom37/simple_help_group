package com.luxand.shg.views.activities.state_officer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.luxand.shg.R;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.views.activities.ForgotPinActivity;
import com.luxand.shg.views.activities.GroupMemberEnrollmentList_Activity;
import com.luxand.shg.views.fragments.department.DepartmentHomeFragment;
import com.luxand.shg.views.fragments.home.DashBoardFragment;
import com.luxand.shg.views.fragments.home.DefaultersFragment;
import com.luxand.shg.views.fragments.home.HomeFragment;
import com.luxand.shg.views.fragments.home.TrainingVideosFragment;
import com.luxand.shg.views.fragments.meeting.MeetingListFragment;

public class StateOfficerMainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public TextView tv_title,toolbar_name,header_name;
    public ImageView header_image,iv_menu_open,iv_menu_back;
    DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_state_officer);
        tv_title = findViewById(R.id.tv_title);
        iv_menu_open = findViewById(R.id.iv_menu_open);
        iv_menu_back = findViewById(R.id.iv_menu_back);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        drawer = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
        drawer.addDrawerListener(drawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_name = headerView.findViewById(R.id.header_name);
        header_image = headerView.findViewById(R.id.header_image);
        header_name.setText("SHG");
        navigationView.setNavigationItemSelectedListener(StateOfficerMainActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);

        tv_title.setText(getResources().getString(R.string.home));
        header_image.setImageResource(R.drawable.ic_user);
        iv_menu_open.setOnClickListener(view -> drawer.openDrawer(GravityCompat.END));
        iv_menu_back.setOnClickListener(view -> onBackPressed());
        setHomeFragment(new DepartmentHomeFragment(),null);
    }


    public void setHomeFragment(Fragment fragment, @Nullable Bundle bundle){
        fragment.setArguments(bundle);
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_department,fragment).addToBackStack("HomeFragment").commit();

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        if (id == R.id.home) {
            setHomeFragment(new DepartmentHomeFragment(),null);
        }if (id == R.id.dashboard) {
            setHomeFragment(new DashBoardFragment(), null);
        }if (id == R.id.enrollment) {
            startActivity(new Intent(this, GroupMemberEnrollmentList_Activity.class));
        }if (id == R.id.meeting) {
            setHomeFragment(new MeetingListFragment(), null);
        }if (id == R.id.tips) {
            setHomeFragment(new DefaultersFragment(), null);
        }
        if (id == R.id.change_password) {
            startActivity(new Intent(StateOfficerMainActivity.this, ForgotPinActivity.class));
        }
        if (id == R.id.logout) {
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_department);
        if(fragment instanceof DepartmentHomeFragment){
            finish();
        } else{
            getSupportFragmentManager().popBackStack();
        }
        //super.onBackPressed();
    }
}
