package com.luxand.shg.views.activities.division_offcer;

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
import com.luxand.shg.views.activities.MainActivity;
import com.luxand.shg.views.fragments.home.SettingsFragment;
import com.luxand.shg.views.fragments.samuha_sakhi.DivisionGroupListFragment;
import com.luxand.shg.views.fragments.samuha_sakhi.LoanSanctionFragment;

public class DivisionOfficerMainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    public TextView tvTitle;
    public ImageView ivMenuOpen, ivMenuBack;
    DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_division_main);
        tvTitle = findViewById(R.id.tv_title);
        ivMenuOpen = findViewById(R.id.iv_menu_open);
        ivMenuBack = findViewById(R.id.iv_menu_back);
        //toolbar_name = findViewById(R.id.tv_title);
        //tvTitle.setText("Meeting");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
        drawer.addDrawerListener(drawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DivisionOfficerMainActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);

        setHomeFragment(new DivisionGroupListFragment(),null);
        ivMenuOpen.setOnClickListener(view -> drawer.openDrawer(GravityCompat.END));
        ivMenuBack.setOnClickListener(view -> onBackPressed());

    }

    public void setHomeFragment(Fragment fragment, @Nullable Bundle bundle){
        fragment.setArguments(bundle);
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_division,fragment).addToBackStack("DivisionFragment").commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        if (id == R.id.dashboard) {
            setHomeFragment(new DivisionGroupListFragment(),null);
        }if (id == R.id.loans) {
            setHomeFragment(new LoanSanctionFragment(), null);
        }if (id == R.id.settings) {
            setHomeFragment(new SettingsFragment(), null);
        }
        if (id == R.id.change_password) {
            startActivity(new Intent(DivisionOfficerMainActivity.this, ForgotPinActivity.class));
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

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_division);
        if(fragment instanceof DivisionGroupListFragment){
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
