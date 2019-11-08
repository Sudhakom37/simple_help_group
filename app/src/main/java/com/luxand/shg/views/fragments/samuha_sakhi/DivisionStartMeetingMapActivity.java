package com.luxand.shg.views.fragments.samuha_sakhi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.BaseActivity;
import com.luxand.shg.views.activities.division_offcer.DivisionOfficerMainActivity;

import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DivisionStartMeetingMapActivity extends BaseActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "DivisionStartMeetingMap";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    LocationManager mLocationManager;
    double longitude,latitude;
    String myAddress;
    Button btStart;
    SupportMapFragment mapFragment;
    MySharedPreference preference;
    TextView tvTitle;
    ImageView ivMenuBack,ivMenuOpen;
    GoogleApiClient gac;
    String subLocality;

    String action;

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_meeting_start);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        btStart = findViewById(R.id.bt_start);
        tvTitle = findViewById(R.id.tv_title);
        ivMenuBack = findViewById(R.id.iv_menu_back);
        ivMenuOpen = findViewById(R.id.iv_menu_open);
        ivMenuOpen.setVisibility(View.GONE);
        ivMenuBack.setVisibility(View.VISIBLE);
        ivMenuBack.setOnClickListener(view -> finish());

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        preference = new MySharedPreference(this);
        action = getIntent().getStringExtra("Action");
        checkForGPS();
        //tvPlace = (TextView) findViewById(R.id.tvPlace);
        tvTitle.setText(action);
        //noinspection deprecation
        gac = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if(action != null&& !action.equalsIgnoreCase("")){
            btStart.setText(action);
        }

        btStart.setOnClickListener(view -> {

        JsonObject object = new JsonObject();
            object.addProperty("meetings_id",preference.getPref(PrefKeys.MEETING_ID));
            object.addProperty("user_id",preference.getPref(PrefKeys.USER_ID));
            object.addProperty("address",myAddress);
            object.addProperty("latitude",latitude);
            object.addProperty("longitude",longitude);
            if(btStart.getText().toString().equalsIgnoreCase("Start Meeting")){
                startMeeting(object);
            }else if(btStart.getText().toString().equalsIgnoreCase("end meeting")){
                stopMeeting(object);
            }

        });

        if (!checkPermissions()) {
            requestPermissions();
        } else {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
                    0, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                    0, this);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: latitude "+latitude+" longitude "+longitude+"google map "+googleMap);
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location)
                .title("Your Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setMyLocationEnabled(true);
        googleMap.setMinZoomPreference(25f);
        googleMap.setMaxZoomPreference(100f);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DivisionStartMeetingMapActivity.this, "Please allow ACCESS_COARSE_LOCATION permission.",
                    Toast.LENGTH_LONG).show();

        }
    }
    private void startMeeting(JsonObject object){

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this).
                getRestAdapter()
                .startMeeting(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SuccessModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onNext(SuccessModel login) {
                        ProgressBarDialog.cancelLoading();

                        Toast.makeText(DivisionStartMeetingMapActivity.this, "Meeting Started", Toast.LENGTH_SHORT).show();
                        /*String group_id=String.valueOf(login.getData().getGroupId());
                        String group_name=""+login.getData().getGroupName();
                        String user_image=""+login.getData().getImage();
                        String role_id = String.valueOf(login.getData().getRoleId());
                        mySharedPreference.setPref(PrefKeys.GROUP_ID,group_id);
                        mySharedPreference.setPref(PrefKeys.GROUP_NAME,group_name);
                        mySharedPreference.setPref(PrefKeys.USER_IMAGE,user_image);*/
                        startActivityBase(DivisionStartMeetingMapActivity.this, DivisionOfficerMainActivity.class);
                        finish();

                    }
                });
    }
    private void stopMeeting(JsonObject object){

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this).
                getRestAdapter()
                .stopMeeting(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SuccessModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onNext(SuccessModel login) {
                        ProgressBarDialog.cancelLoading();

                        Toast.makeText(DivisionStartMeetingMapActivity.this, "Meeting Ended", Toast.LENGTH_SHORT).show();
                        /*String group_id=String.valueOf(login.getData().getGroupId());
                        String group_name=""+login.getData().getGroupName();
                        String user_image=""+login.getData().getImage();
                        String role_id = String.valueOf(login.getData().getRoleId());
                        mySharedPreference.setPref(PrefKeys.GROUP_ID,group_id);
                        mySharedPreference.setPref(PrefKeys.GROUP_NAME,group_name);
                        mySharedPreference.setPref(PrefKeys.USER_IMAGE,user_image);*/
                        startActivityBase(DivisionStartMeetingMapActivity.this, DivisionOfficerMainActivity.class);
                        finish();

                    }
                });
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        if (checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude =location.getLongitude();
        mapFragment.getMapAsync(this);
        // time=location.getTime();
        //location.
        String time_new = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS",Locale.ENGLISH).format(location.getTime());

        if( location.getProvider().equals(android.location.LocationManager.GPS_PROVIDER))
            Log.d("Location", "Time GPS: " + time_new); // This is what we want!
        else
            Log.d("Location", "Time Device (" + location.getProvider() + "): " + time_new);


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses == null || addresses.isEmpty())
                return;

            //map.clear();
            Address address = addresses.get(0);
            if (address == null) {
                myAddress = "My Location";
            } else {
                String locality = address.getLocality();
                subLocality = address.getSubLocality();


                if (locality == null && subLocality == null) {
                    myAddress = "";
                } else if (locality == null) {
                    myAddress = subLocality;
                } else if (subLocality == null) {
                    myAddress = locality;
                } else {
                    myAddress = subLocality + "," + locality;
                }
            }
            Log.d(TAG, "onLocationChanged: "+myAddress);
            //txt_address.setText(myAddress+Latitude+","+ Longitude +", "+time_new);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public  void checkForGPS() {



        // Get Location Manager and check for GPS & Network location services
        // LocationManager lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||
                !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gac != null)
            gac.connect();
    }

    @Override
    protected void onStop() {
        if (gac != null && gac.isConnected()) {
            gac.disconnect();
        }
        super.onStop();
    }

}
