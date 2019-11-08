package com.luxand.shg.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.luxand.shg.BuildConfig;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.GroupDatfile;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.services.DownloadGroupDatFile;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.fragments.home.DashBoardFragment;
import com.luxand.shg.views.fragments.home.HomeFragment;
import com.luxand.shg.views.fragments.home.SettingsFragment;
import com.luxand.shg.views.fragments.home.TipsFragment;
import com.luxand.shg.views.fragments.home.TrainingVideosFragment;
import com.luxand.shg.views.fragments.meeting.MeetingListFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    public TextView tv_title,header_name;
    public ImageView header_image,iv_menu_open,iv_menu_back,ivHomeBack;
    DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;
    MySharedPreference preference;
    public static List<GroupDatfile.DatUrl> datUrl;
    MainActivity.ResponseReceiver receiver = new MainActivity.ResponseReceiver();
    ProgressBar progressBar_per;
    Dialog d_per_download;
    TextView tv_id;
    String group_id;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_title = findViewById(R.id.tv_title);
        iv_menu_open = findViewById(R.id.iv_menu_open);
        iv_menu_back = findViewById(R.id.iv_menu_back);
        ivHomeBack = findViewById(R.id.iv_home_logo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preference = new MySharedPreference(this);
        group_id=preference.getPref(PrefKeys.GROUP_ID);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getClassDatFile();
        drawer = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
        drawer.addDrawerListener(drawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_name = headerView.findViewById(R.id.header_name);
        header_image = headerView.findViewById(R.id.header_image);
        header_name.setText(R.string.app_name);
        header_name.setText(preference.getPref(PrefKeys.NAME));
        Picasso.with(this)
                .load(preference.getPref(PrefKeys.USER_IMAGE))
                .placeholder(R.drawable.ic_user)
                .transform(new CircleTransform())
                .rotate(270f)
                .into(header_image);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);

        tv_title.setText(getResources().getString(R.string.home));
        header_image.setImageResource(R.drawable.ic_user);
        iv_menu_open.setOnClickListener(view -> drawer.openDrawer(GravityCompat.END));
        iv_menu_back.setOnClickListener(view -> onBackPressed());
        ivHomeBack.setOnClickListener(view -> setHomeFragment(new HomeFragment(),null));
        setHomeFragment(new HomeFragment(),null);
    }


    public void setHomeFragment(Fragment fragment,@Nullable Bundle bundle){
        fragment.setArguments(bundle);
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_home,fragment).addToBackStack("HomeFragment").commit();

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        if (id == R.id.home) {
            setHomeFragment(new HomeFragment(),null);
        }if (id == R.id.dashboard) {
            setHomeFragment(new DashBoardFragment(), null);
        }if (id == R.id.enrollment) {
            startActivity(new Intent(this, GroupMemberEnrollmentList_Activity.class));
        }if (id == R.id.meeting) {
            setHomeFragment(new MeetingListFragment(), null);
        }if (id == R.id.tips) {
            setHomeFragment(new TipsFragment(), null);
        }if (id == R.id.settings) {
            setHomeFragment(new SettingsFragment(), null);
        }
        if (id == R.id.change_password) {
            startActivity(new Intent(MainActivity.this, ForgotPinActivity.class));
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

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_home);
        if(fragment instanceof HomeFragment){
            /*Intent intent = new Intent(Intent.ACTION_MAIN);
            //to clear all old opened activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            finish();
        }else if(fragment instanceof TrainingVideosFragment) {
            iv_menu_back.setVisibility(View.GONE);
            iv_menu_open.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack();
        }
            else{
                getSupportFragmentManager().popBackStack();
            }
        //super.onBackPressed();
    }
    public void getClassDatFile() {

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this).getRestAdapter()
                .download_staff_dat(preference.getPref(PrefKeys.GROUP_ID),RetrofitInstance.getMacAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GroupDatfile>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.d(TAG, "onError: "+e.getMessage());

                    }

                    @Override
                    public void onNext(GroupDatfile groupDatfile) {
                        Log.d(TAG, "onNext: groupDatFile "+groupDatfile.getData().getIsDownloadDat());
                        ProgressBarDialog.cancelLoading();
                        datUrl=groupDatfile.getData().getDatUrl();

                        if (groupDatfile.getData().getIsDownloadDat()==1) {

                            Intent intent = new Intent(getApplicationContext(), DownloadGroupDatFile.class);
                            startService(intent);


                        }

                    }
                });
    }

    public void updateDownloadDatStatus() {

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .updateDownloadDatStatus(preference.getPref(PrefKeys.GROUP_ID),RetrofitInstance.getMacAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<SuccessModel>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }

                    @Override
                    public void onNext(SuccessModel successModel) {
                        ProgressBarDialog.cancelLoading();
                        Log.d(TAG, "onNext: successModel"+successModel.getMsg());
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver,new IntentFilter(
                GroupMemberEnrollmentList_Activity.ResponseReceiver.ACTION_RESP));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = BuildConfig.APPLICATION_ID;

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: "+intent.getAction());
            int value = intent.getIntExtra("per", -1);
            if (value <= 0) {
                if (d_per_download == null) {
                    d_per_download = new Dialog(MainActivity.this);
                    if (!d_per_download.isShowing()) {
                        d_per_download.setContentView(R.layout.download_progress_dialog);
                        progressBar_per =  d_per_download.findViewById(R.id.pb_id);
                        progressBar_per.setMax(100);
                        d_per_download.setCancelable(false);
                        d_per_download.setCanceledOnTouchOutside(false);
                        tv_id =  d_per_download.findViewById(R.id.tv_id);
                        d_per_download.show();
                    }
                } else {
                    if (!(d_per_download.isShowing())) {
                        d_per_download.show();// or alert.dismiss() it
                    }

                }
            }
            try {
                progressBar_per.setProgress(value);
                tv_id.setText(String.valueOf(value));
            } catch (Exception e) {
                e.printStackTrace();
            }

            d_per_download.cancel();
            d_per_download.dismiss();

            updateDownloadDatStatus();



        }
    }


}
