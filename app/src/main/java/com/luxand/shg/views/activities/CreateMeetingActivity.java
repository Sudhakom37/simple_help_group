package com.luxand.shg.views.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.GridDividerDecoration;
import com.luxand.shg.views.attandence.Student_Face_Attendance_SingleDat;
import com.luxand.shg.db.Staff_Attendance_StudentsList_Local;
import com.luxand.shg.db.Staff_Attendance_StudentsList_Local_Table;
import com.luxand.shg.db.Student_Attendance_Local;
import com.luxand.shg.db.Student_Attendance_Local_Table;
import com.luxand.shg.db.Students_TwinsList_Local;
import com.luxand.shg.db.Students_TwinsList_Local_Table;
import com.luxand.shg.helper.Helper;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.helper.RecyclerTouchListener;
import com.luxand.shg.helper.Twin;
import com.luxand.shg.model.Attendance;
import com.luxand.shg.model.GroupMember;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.viewmodel.GroupMembersListViewModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateMeetingActivity extends BaseActivity implements LocationListener {

    private static final String TAG = "CreateMeetingActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    RecyclerView rv_members_list;
    Button button_submit;
    TextView tv_group_members_list, toolbar_name;
    public static List<Twin.Data> list_identical = new ArrayList<>();
    public static List<GroupMember.Data> mstudentlist = new ArrayList<>();
    List<Student_Attendance_Local> list_student_present;
    GroupMembersListViewModel viewModel;
    MySharedPreference preference;
    ImageView iv_camera, iv_back, iv_menu_open;
    private Adapter_Students adapter;
    String myAddress;
    double Longitude, Latitude;
    public static boolean network;
    public static boolean isNetwork;
    LocationManager mLocationManager;

    public CreateMeetingActivity() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enrollment_list);
        rv_members_list = findViewById(R.id.rv_members_list);
        button_submit = findViewById(R.id.button_submit);
        toolbar_name = findViewById(R.id.tv_title);
        tv_group_members_list = findViewById(R.id.tv_group_members_list);
        iv_camera = findViewById(R.id.iv_camera);
        iv_back = findViewById(R.id.iv_menu_back);
        iv_menu_open = findViewById(R.id.iv_menu_open);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        iv_menu_open.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(view -> finish());
        preference = new MySharedPreference(this);
        viewModel = ViewModelProviders.of(this).get(GroupMembersListViewModel.class);

        rv_members_list.setLayoutManager(new GridLayoutManager(this, 3));
        rv_members_list.addItemDecoration(new GridDividerDecoration(this));
        isNetwork = isNetWorkAvailable();
        if (isNetWorkAvailable()) {
            getIdenticalStudents();
        } else {
            show_twinslist_localdb();
        }
        toolbar_name.setText(R.string.meeting);
        checkForGPS();
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
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, this);

        }

        rv_members_list.addOnItemTouchListener(new RecyclerTouchListener(this, rv_members_list, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                /*Intent intent = new Intent(getActivity(), GroupMemberAttandenceActivity.class);
                startActivity(intent);*/
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        iv_camera.setOnClickListener(v -> {

            Intent intent = new Intent(CreateMeetingActivity.this, Student_Face_Attendance_SingleDat.class);
            //intent.putParcelableArrayListExtra("",mstudentlist);
            startActivity(intent);
        });

        button_submit.setOnClickListener(v -> {
            network = isNetWorkAvailable();
            if (network) {
                List<Student_Attendance_Local> list_students = SQLite.select().from(Student_Attendance_Local.class)
                        .where(Student_Attendance_Local_Table.group_id.is(preference.getPref(PrefKeys.GROUP_ID))).queryList();

                if (list_students.size() > 0) {

                    JsonObject object = new JsonObject();
                    JsonArray array = new JsonArray();
                    for (int i = 0; i < list_students.size(); i++) {
                        JsonObject object1 = new JsonObject();
                        object1.addProperty("uuid", "" + list_students.get(i).getStudent_id());
                        object1.addProperty("date", "" + list_students.get(i).getDate_time());

                        array.add(object1);
                    }
                    object.add("users", array);
                    object.addProperty("group_id", "" + preference.getPref(PrefKeys.GROUP_ID));
                    object.addProperty("mac_address", "" + RetrofitInstance.getMacAddress());

                    Save_Student_Attendance(object);
                } else {
                    Toast.makeText(CreateMeetingActivity.this, "Please take atleast one person attendance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CreateMeetingActivity.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void Save_Student_Attendance(JsonObject object)
    {
        Log.d(TAG, "Save_Student_Attendance: "+object.toString());
        ProgressBarDialog.showLoadingDialog(CreateMeetingActivity.this);
       RetrofitInstance.getInstance(this).getRestAdapter()
                .Save_Member_Attendance(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Attendance>() {
                    @Override
                    public void onCompleted() {
                        getGroupList(viewModel);

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();

                    }

                    @Override
                    public void onNext(Attendance attendance) {


                        Log.d(TAG, "onNext: successModel"+attendance.toString());
                        ProgressBarDialog.cancelLoading();
                        if (attendance.getSuccess()) {

                            Toast.makeText(CreateMeetingActivity.this, attendance.getMessage(), Toast.LENGTH_SHORT).show();
                            SQLite.delete(Student_Attendance_Local.class)
                                    .where(Student_Attendance_Local_Table.group_id.is(preference.getPref(PrefKeys.GROUP_ID)))
                                    .async()
                                    .execute();
                            preference.setPref(PrefKeys.MEETING_ID,String.valueOf(attendance.getData().getMeetingsId()));
                            startActivityBase(CreateMeetingActivity.this, MeetingPhotosActivity.class);
                            finish();

                        }else{
                            Toast.makeText(CreateMeetingActivity.this, attendance.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    private void getGroupList(GroupMembersListViewModel viewModel){
        viewModel.getData().observe(Objects.requireNonNull(this), groupMember -> {

            if(groupMember!=null && groupMember.getData() != null) {
                Log.d(TAG, "onChanged: "+groupMember.getData());
                mstudentlist = groupMember.getData();
                tv_group_members_list.setText(String.format(Locale.ENGLISH,"Start a Meeting  (%d Members)", groupMember.getData().size()));
                adapter = new Adapter_Students(mstudentlist, CreateMeetingActivity.this);
                rv_members_list.setAdapter(adapter);

            }
        });
    }

    private void getIdenticalStudents(){

        RetrofitInstance.getInstance(this).getRestAdapter()
                .getClassIdenticalStudents(preference.getPref(PrefKeys.GROUP_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Twin>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: getMessage"+e.getMessage() );
                    }

                    @Override
                    public void onNext(Twin twins) {
                        Log.d(TAG, "onNext:  twins.size"+twins.getData().size());

                        list_identical=twins.getData();
                        insert_twinslist_localdb();
                    }
                });

    }

    private void insert_twinslist_localdb(){

        SQLite.delete(Students_TwinsList_Local.class).where(Students_TwinsList_Local_Table.section_id.is(preference.getPref(PrefKeys.GROUP_ID)))
                .async()
                .execute();

        String st_data = JSON.toJSONString(list_identical);
        Students_TwinsList_Local twinsList_local=new Students_TwinsList_Local();
        twinsList_local.setSection_id(preference.getPref(PrefKeys.GROUP_ID));
        twinsList_local.setData(st_data);
        twinsList_local.save();

    }

    private void show_twinslist_localdb(){
        Students_TwinsList_Local data = SQLite.select().from(Students_TwinsList_Local.class)
                .where(Students_TwinsList_Local_Table.section_id.is(preference.getPref(PrefKeys.GROUP_ID)))
                .querySingle();
        if(data!=null){
            list_identical = JSON.parseArray(data.getData(), Twin.Data.class);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        showData();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void showData() {
        if (Helper.isNetWorkAvailable(this)) {
            viewModel.getGroupMemberList("1");
            getGroupList(viewModel);
        } else {
            show_studentslist_localdb();
        }
    }

    private void show_studentslist_localdb(){

        Staff_Attendance_StudentsList_Local data = SQLite.select().from(Staff_Attendance_StudentsList_Local.class)
                .where(Staff_Attendance_StudentsList_Local_Table.section_id.is(preference.getPref(PrefKeys.GROUP_ID)))
                .querySingle();

        if(data!=null){
            mstudentlist = JSON.parseArray(data.getData(), GroupMember.Data.class);
            RemovePresentStudents();
            Log.d(TAG, "show_studentslist_localdb: "+mstudentlist.size());
            adapter = new Adapter_Students(mstudentlist, CreateMeetingActivity.this);
            rv_members_list.setAdapter(adapter);
        }
    }
    public void RemovePresentStudents() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        String formattedDate = sdf.format(new Date());
        list_student_present = SQLite.select().from(Student_Attendance_Local.class)
                .where(Student_Attendance_Local_Table.date.is(formattedDate))
                .queryList();

        if (list_student_present.size() > 0) {
            for (int i = 0; i < mstudentlist.size(); i++) {
                for (int j = 0; j < list_student_present.size(); j++) {
                    String st_original = String.valueOf(mstudentlist.get(i).getUuid());

                    if (list_student_present.get(j).getStudent_id().equalsIgnoreCase(st_original)) {
                        mstudentlist.remove(i);
                    }
                }
            }
        }
    }
    public class Adapter_Students extends RecyclerView.Adapter<Adapter_Students.ViewHolder> implements Filterable {
        List<GroupMember.Data> data_list ;
        Context context;
        List<GroupMember.Data> mOriginalValues ;

        Adapter_Students(List<GroupMember.Data> data_list, Context context) {
            this.data_list = data_list;
            this.context = context;
            this.mOriginalValues = data_list;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView stu_name ;
            ImageView imgg;
            ImageView undo_button,attended;
            RelativeLayout rel_parent;

            public ViewHolder(View v) {
                super(v);
                stu_name =  v.findViewById(R.id.stu_name);
                imgg =  v.findViewById(R.id.imgg);
                rel_parent =  v.findViewById(R.id.rel_parent);
                undo_button =  v.findViewById(R.id.undo_button);
                attended =  v.findViewById(R.id.attended);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_attendance_students, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
            String formattedDate = sdf.format(new Date());
            List<Student_Attendance_Local> list_student_present = SQLite.select().from(Student_Attendance_Local.class)
                    .where(Student_Attendance_Local_Table.date.is(formattedDate))
                    .queryList();


            viewHolder.stu_name.setText(String.format("%s", data_list.get(position).getName()));
            String st_url = data_list.get(position).getImage();

            if (st_url!=null && st_url.length() == 0) {

                Picasso.with(context).load(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .centerCrop()
                        .fit()
                        .placeholder(R.drawable.ic_user)
                        .into(viewHolder.imgg);
            } else {
                if (data_list.get(position).getCameraConfig()!=null && data_list.get(position).getCameraConfig().equalsIgnoreCase("front")) {
                    try {
                        Picasso.with(context)
                                .load(data_list.get(position).getImage())
                                .error(R.drawable.ic_user)
                                .fit()
                                .transform(new CircleTransform())
                                .rotate(270f)
                                .placeholder(R.drawable.ic_user)
                                .into(viewHolder.imgg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (data_list.get(position).getCameraConfig()!=null && data_list.get(position).getCameraConfig().equalsIgnoreCase("back")) {
                    try {
                        Picasso.with(context)
                                .load(data_list.get(position).getImage())
                                .error(R.drawable.ic_user)
                                .fit()
                                .transform(new CircleTransform())
                                .rotate(90f)
                                .placeholder(R.drawable.ic_user)
                                .into(viewHolder.imgg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }}

            if (list_student_present.size() > 0) {

                List<Boolean> list = new ArrayList<>();
                list.clear();

                for (int j = 0; j < data_list.size(); j++) {
                    if (data_list.get(j).getIsPresent()!=null && data_list.get(j).getIsPresent().equalsIgnoreCase("1")) {
                        list.add(true);
                    } else {
                        list.add(false);
                    }
                }

                for (int i = 0; i < data_list.size(); i++) {
                    for (int j = 0; j < list_student_present.size(); j++) {
                        String st_original = String.valueOf(data_list.get(i).getUuid());

                        if (list_student_present.get(j).getStudent_id().equalsIgnoreCase(st_original)) {
                            list.set(i, true);
                            viewHolder.attended.setVisibility(View.VISIBLE);
                        }else{
                            viewHolder.attended.setVisibility(View.GONE);
                        }

                    }
                }

                if (list.get(position)) {
                    viewHolder.attended.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.attended.setVisibility(View.GONE);
                }

            } else {

                if (data_list.get(position).getIsPresent()!=null && data_list.get(position).getIsPresent().equalsIgnoreCase("1")) {
                    viewHolder.attended.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.attended.setVisibility(View.GONE);
                }
            }

            viewHolder.rel_parent.setOnClickListener(v -> {

                preference.setPref(PrefKeys.UUID, "" + data_list.get(position).getUuid());
                preference.setPref(PrefKeys.MEMBER_NAME, "" + data_list.get(position).getName());
                preference.setPref(PrefKeys.MEMBER_IMAGE, "" + data_list.get(position).getImage());

                if (data_list.get(position).getIsEnrolled()!=null && !data_list.get(position).getIsEnrolled().equalsIgnoreCase("1")) {
                    //startActivity(new Intent(getApplicationContext(), MeetingPhotosActivity.class))
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateMeetingActivity.this);
                    alertDialog.setMessage(getResources().getString(R.string.facenotregistered));
                    alertDialog.setPositiveButton("OK", (dialog, which) -> {

                        Dialog d = alertDialog.create();
                        d.dismiss();
                    });

                    alertDialog.show();
                }

            });
        }

        @Override
        public int getItemCount() {
            return data_list.size();
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    if (charSequence == null || charSequence.length() == 0) {
                        results.values = mOriginalValues;
                        results.count = mOriginalValues.size();
                    } else {
                        List<GroupMember.Data> filterResultsData = new ArrayList<>();


                        for (GroupMember.Data c : mOriginalValues) {
                            if (c.getName().toLowerCase(Locale.ENGLISH).contains(charSequence) ||String.valueOf( c.getUuid()).toLowerCase(Locale.ENGLISH).contains(charSequence)) {
                                filterResultsData.add(c);
                            }
                        }


                        results.values = filterResultsData;
                        results.count = filterResultsData.size();
                    }

                    return results;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    data_list = (ArrayList<GroupMember.Data>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        if (checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Latitude=location.getLatitude();
        Longitude =location.getLongitude();
        // time=location.getTime();
        //location.
        String time_new = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS",Locale.ENGLISH).format(location.getTime());

        if( location.getProvider().equals(android.location.LocationManager.GPS_PROVIDER))
            Log.d("Location", "Time GPS: " + time_new); // This is what we want!
        else
            Log.d("Location", "Time Device (" + location.getProvider() + "): " + time_new);


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        try {
            List<Address> addresses = geocoder.getFromLocation(Latitude, Longitude, 1);

            if (addresses == null || addresses.isEmpty())
                return;

            //map.clear();
            Address address = addresses.get(0);
            if (address == null) {
                myAddress = "My Location";
            } else {
                String locality = address.getLocality();
                String subLocality = address.getSubLocality();


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


}
