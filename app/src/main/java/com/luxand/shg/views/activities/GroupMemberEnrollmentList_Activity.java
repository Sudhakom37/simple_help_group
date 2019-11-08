package com.luxand.shg.views.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.luxand.shg.BuildConfig;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.db.Student_EnroolList_Local;
import com.luxand.shg.db.Student_EnroolList_Local_Table;
import com.luxand.shg.views.enrollment.Student_Face_Enroll;
import com.luxand.shg.helper.GridDividerDecoration;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.GroupMember;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.util.CircleTransform;
import com.luxand.shg.util.ProgressBarDialog;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class GroupMemberEnrollmentList_Activity extends BaseActivity {

    private static final String TAG = "Students_EnrollmentList";
    private MySharedPreference preferences;
    String macAddress, group_id, section_id;
    RecyclerView recyclerView;
    TextView toolbar_name;
    ImageView iv_search, ivAdd;
    LinearLayout linear_spinner;
    RelativeLayout rel_search;
    EditText search_content;
    TextView tvGroupMembers;
    List<Student_EnroolList_Local> offline_list_students = new ArrayList<>();
    public static List<GroupMember.Data> registeredUsersList = new ArrayList<>();
    StudentsListAdapter mUserListAdapter;
    int type = 0;
    ImageView iv_back, iv_menu_open;
    ResponseReceiver receiver = new ResponseReceiver();
    ProgressBar progressBar_per;
    Dialog d_per_download;
    TextView tv_id;
    Button bt_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_student_enrollment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        toolbar_name = findViewById(R.id.tv_title);
        tvGroupMembers = findViewById(R.id.tv_group_members);
        iv_search = findViewById(R.id.iv_search);
        linear_spinner = findViewById(R.id.linear_spinner);
        rel_search = findViewById(R.id.rel_search);
        search_content = findViewById(R.id.search_content);
        ivAdd = findViewById(R.id.iv_add);
        bt_submit = findViewById(R.id.bt_submit);
        iv_back = findViewById(R.id.iv_menu_back);
        iv_menu_open = findViewById(R.id.iv_menu_open);
        iv_menu_open.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupMemberEnrollmentList_Activity.this));
        recyclerView.addItemDecoration(new GridDividerDecoration(GroupMemberEnrollmentList_Activity.this));
        macAddress = RetrofitInstance.getMacAddress();
        preferences = new MySharedPreference(GroupMemberEnrollmentList_Activity.this);

        group_id = preferences.getPref(PrefKeys.GROUP_ID);
        toolbar_name.setText(getResources().getString(R.string.enrollment));
        bt_submit.setOnClickListener(view -> {
            if (isNetWorkAvailable()) {
                checkLock();
            } else {
                finish();
            }
        });
        iv_back.setOnClickListener(view -> {
            type = 1;
            if (isNetWorkAvailable()) {
                checkLock();
            } else {
                finish();
            }

        });

        ivAdd.setOnClickListener(view -> startActivityBase(GroupMemberEnrollmentList_Activity.this, AddGroupMemberActivity.class));
        getGroupMemberList("1");

        iv_search.setOnClickListener(v -> {
            if (rel_search.getVisibility() == View.VISIBLE) {
                search_content.setText("");
                rel_search.setVisibility(View.GONE);
                linear_spinner.setVisibility(View.VISIBLE);
            } else {
                rel_search.setVisibility(View.VISIBLE);
                linear_spinner.setVisibility(View.GONE);
            }

        });
        if (!isNetWorkAvailable()) {
            rel_search.setVisibility(View.GONE);
            linear_spinner.setVisibility(View.GONE);

        }

        search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (registeredUsersList.size() != 0) {
                    mUserListAdapter.getFilter().filter(s.toString());
                }
            }
        });


    }

    private void syncData() {
        if (isNetWorkAvailable()) {
            offline_list_students.clear();
            offline_list_students = SQLite.select().from(Student_EnroolList_Local.class)
                    .where(Student_EnroolList_Local_Table.group_id.eq(preferences.getPref(PrefKeys.GROUP_ID)))
                    .and(Student_EnroolList_Local_Table.isEnrolled.is("1"))
                    .and(Student_EnroolList_Local_Table.isEnrolledLocal.is("1"))
                    .queryList();

            if (offline_list_students.size() > 0) {

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                JsonArray array = new JsonArray();
                JsonObject final_object = new JsonObject();
                final_object.addProperty("group_id", preferences.getPref(PrefKeys.GROUP_ID));
                final_object.addProperty("mac_address", "" + RetrofitInstance.getMacAddress());
                //final_object.addProperty("school_class_section_id", preferences.getPref(PrefKeys.CLASS_SECTION_ID));

                for (int i = 0; i < offline_list_students.size(); i++) {
                    JsonObject object = new JsonObject();
                    object.addProperty("uuid", offline_list_students.get(i).getUuid());
                    object.addProperty("camera_config", "" + offline_list_students.get(i).getCamera_config());
                    array.add(object);

                    if (!offline_list_students.get(i).getImage().equalsIgnoreCase("")) {
                        File file = new File(offline_list_students.get(i).getImage());
                        builder.addFormDataPart("images[]", file.getName(),
                                RequestBody.create(okhttp3.MediaType.parse("image/*"), file));
                    }
                    if (i == 0) {
                        if (offline_list_students.get(i).getSectiondatfile() != null && offline_list_students.get(i).getSectiondatfile().length() != 0) {
                            File dat_file = new File(offline_list_students.get(i).getSectiondatfile());
                            builder.addFormDataPart("group_dat_file", dat_file.getName(),
                                    RequestBody.create(okhttp3.MediaType.parse("file/*"), dat_file));
                        }
                    }

                    /*if(offline_list_students.get(i).getStudentDatfile()!=null&&offline_list_students.get(i).getStudentDatfile().length()!=0){
                    File dat_file = new File(offline_list_students.get(i).getStudentDatfile());
                    if(dat_file.exists()){
                        builder.addFormDataPart("dat_files[]", dat_file.getName(),
                                RequestBody.create(okhttp3.MediaType.parse("file/*"), dat_file));
                    }}*/
                }

                final_object.add("data", array);
                Log.d(TAG, "syncData: " + final_object.toString());
                builder.addFormDataPart("content", final_object.toString());

                MultipartBody requestBody = builder.build();
                SendEnrolledStudentsToServer(requestBody, GroupMemberEnrollmentList_Activity.this);
            } else {
                Toast.makeText(GroupMemberEnrollmentList_Activity.this, "Offline data not available", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void SendEnrolledStudentsToServer(RequestBody body, Context mContext) {
        Log.d(TAG, "SendEnrolledStudentsToServer: " + body.toString());
        ProgressBarDialog.showLoadingDialog(mContext);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .SendEnrolledStudentsToServer(body)
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
                    public void onNext(SuccessModel successModel) {
                        Log.d(TAG, "onNext: successModel " + successModel.getStatus());
                        ProgressBarDialog.cancelLoading();
                        //Toast.makeText(mContext, ""+successModel.getMsg(), Toast.LENGTH_SHORT).show();
                        if (type == 1) {
                            if (successModel.getStatus()) {
                                SQLite.delete(Student_EnroolList_Local.class).where(Student_EnroolList_Local_Table.group_id.is(preferences.getPref(PrefKeys.GROUP_ID)))
                                        .async()
                                        .execute();
                            }
                            finish();
                        } else {
                            if (successModel.getStatus()) {
                                SQLite.delete(Student_EnroolList_Local.class).where(Student_EnroolList_Local_Table.group_id.is(preferences.getPref(PrefKeys.GROUP_ID)))
                                        .async()
                                        .execute();
                            }
                        }

                    }
                });

    }

    public void showData() {
        if (isNetWorkAvailable()) {

            List<Student_EnroolList_Local> list_students = SQLite.select().from(Student_EnroolList_Local.class)
                    .where(Student_EnroolList_Local_Table.isEnrolledLocal.is("1"))
                    .and(Student_EnroolList_Local_Table.group_id.is(preferences.getPref(PrefKeys.GROUP_ID))).queryList();

            if (list_students.size() > 0) {
                show_Offlinedata();
            }
        } else {
            show_Offlinedata();
        }
    }

    private void show_Offlinedata() {
        registeredUsersList.clear();
        List<Student_EnroolList_Local> list_students = SQLite.select().from(Student_EnroolList_Local.class)
                .where(Student_EnroolList_Local_Table.group_id.is(preferences.getPref(PrefKeys.GROUP_ID))).queryList();

        for (int i = 0; i < list_students.size(); i++) {
            GroupMember.Data registeredUser = new GroupMember.Data();
            registeredUser.setName(list_students.get(i).getName());
            registeredUser.setGender(list_students.get(i).getGender());
            registeredUser.setImage(list_students.get(i).getImage());
            registeredUser.setIsEnrolled(list_students.get(i).getIsEnrolled());
            registeredUser.setGroupId(Integer.valueOf(list_students.get(i).getSchoolId()));
            registeredUser.setStatus(list_students.get(i).getPstatus());
            registeredUser.setUuid(Integer.valueOf(list_students.get(i).getUuid()));
            registeredUser.setCameraConfig(list_students.get(i).getCamera_config());

            registeredUsersList.add(registeredUser);
        }

        mUserListAdapter = new StudentsListAdapter(GroupMemberEnrollmentList_Activity.this, registeredUsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupMemberEnrollmentList_Activity.this));
        recyclerView.setAdapter(mUserListAdapter);
    }

    private void studentlist_Offline() {

        SQLite.delete(Student_EnroolList_Local.class).where(Student_EnroolList_Local_Table.group_id.is(preferences.getPref(PrefKeys.GROUP_ID)))
                .async()
                .execute();

        for (int i = 0; i < registeredUsersList.size(); i++) {
            Student_EnroolList_Local classList = new Student_EnroolList_Local();
            classList.setData("");
            classList.setGroupId(preferences.getPref(PrefKeys.GROUP_ID));
            classList.setName(registeredUsersList.get(i).getName());
            classList.setGender(registeredUsersList.get(i).getGender());
            classList.setImage(registeredUsersList.get(i).getImage());
            classList.setIsEnrolled(registeredUsersList.get(i).getIsEnrolled());
            classList.setSchoolId(String.valueOf(registeredUsersList.get(i).getGroupId()));
            classList.setPstatus(registeredUsersList.get(i).getStatus());
            classList.setUuid(String.valueOf(registeredUsersList.get(i).getUuid()));
            classList.setSectiondatfile("");
            classList.setStudentdatfile("");
            classList.setCamera_config(registeredUsersList.get(i).getCameraConfig());
            classList.save();
        }

    }

    /* private void getEnrollStudentsList(String id){
         registeredUsersList.clear();

         ProgressBarDialog.showLoadingDialog(this);
         Helper.getdataInstance(this).
                 getRetrofit()
                 .getEnrollStudentsList(id)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Subscriber<List<StudentEnrollmentListModel>>() {
                     @Override
                     public void onCompleted() {
                         if(registeredUsersList.size() > 0){

                             List<Student_EnroolList_Local> list_students = SQLite.select().from(Student_EnroolList_Local.class)
                                     .where(Student_EnroolList_Local_Table.isEnrolledLocal.is("1"))
                                     .and(Student_EnroolList_Local_Table.class_section_id.is(preferences.getPref(PrefKeys.GROUP_ID))).queryList();


                             if(list_students.size()==0) {
                                 studentlist_Offline();
                             }

                             mUserListAdapter = new StudentsListAdapter(GroupMemberEnrollmentList_Activity.this,registeredUsersList);
                             recyclerView.setLayoutManager(new LinearLayoutManager(GroupMemberEnrollmentList_Activity.this));
                             recyclerView.setAdapter(mUserListAdapter);

                         }
                     }

                     @Override
                     public void onError(Throwable e) {
                         ProgressBarDialog.cancelLoading();
                         String msg = e.getMessage();
                         Log.d("error", msg);
                     }

                     @Override
                     public void onNext(List<StudentEnrollmentListModel> data) {
                         ProgressBarDialog.cancelLoading();

                         registeredUsersList = data;

                     }
                 });

     }*/
    public void getGroupMemberList(String group_id) {

        RetrofitInstance.getInstance(this).getRestAdapter()
                .getGroupMemberList(group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GroupMember>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();


                        if (registeredUsersList.size() > 0) {

                            List<Student_EnroolList_Local> list_students = SQLite.select().from(Student_EnroolList_Local.class)
                                    .where(Student_EnroolList_Local_Table.isEnrolledLocal.is("1"))
                                    .and(Student_EnroolList_Local_Table.group_id.is(preferences.getPref(PrefKeys.GROUP_ID))).queryList();


                            if (list_students.size() == 0) {
                                studentlist_Offline();
                            }

                            mUserListAdapter = new StudentsListAdapter(GroupMemberEnrollmentList_Activity.this, registeredUsersList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(GroupMemberEnrollmentList_Activity.this));
                            recyclerView.setAdapter(mUserListAdapter);

                        } else {
                            mUserListAdapter = new StudentsListAdapter(GroupMemberEnrollmentList_Activity.this, registeredUsersList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(GroupMemberEnrollmentList_Activity.this));
                            recyclerView.setAdapter(mUserListAdapter);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GroupMember groupMember) {
                        if (groupMember != null) {
                            registeredUsersList = groupMember.getData();
                            tvGroupMembers.setText(String.format(Locale.ENGLISH, "Group Members ( %d Members)", registeredUsersList.size()));
                            Log.d(TAG, "onNext: registeredUsersList" + groupMember.getData().get(0).getIsEnrolled());
                        }

                    }
                });

    }

   /* public void getClassDatFile() {

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this).getRestAdapter()
                .download_staff_dat(preferences.getPref(PrefKeys.GROUP_ID),RetrofitInstance.getMacAddress())
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
                        Log.d(TAG, "onNext: groupDatfile "+groupDatfile.getData().getIsDownloadDat());
                        ProgressBarDialog.cancelLoading();
                        datUrl=groupDatfile.getData().getDatUrl();

                        if (groupDatfile.getData().getIsDownloadDat()==1) {

                            Intent intent = new Intent(getApplicationContext(), DownloadGroupDatFile.class);
                            startService(intent);


                        }

                    }
                });
    }*/

    public void updateDownloadDatStatus() {

        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .updateDownloadDatStatus(preferences.getPref(PrefKeys.GROUP_ID), RetrofitInstance.getMacAddress())
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
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(SuccessModel successModel) {
                        ProgressBarDialog.cancelLoading();
                        Log.d(TAG, "onNext: successModel" + successModel.getMsg());
                    }
                });

    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = BuildConfig.APPLICATION_ID;

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: " + intent.getAction());
            int value = intent.getIntExtra("per", -1);
            if (value <= 0) {
                if (d_per_download == null) {
                    d_per_download = new Dialog(GroupMemberEnrollmentList_Activity.this);
                    if (!d_per_download.isShowing()) {
                        d_per_download.setContentView(R.layout.download_progress_dialog);
                        progressBar_per = d_per_download.findViewById(R.id.pb_id);
                        progressBar_per.setMax(100);
                        d_per_download.setCancelable(false);
                        d_per_download.setCanceledOnTouchOutside(false);
                        tv_id = d_per_download.findViewById(R.id.tv_id);
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

    public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.ViewHolder> implements Filterable {
        Context mContext;
        List<GroupMember.Data> list;
        List<GroupMember.Data> mOriginalValues;

        StudentsListAdapter(Context mContext, List<GroupMember.Data> list) {
            this.mContext = mContext;
            this.list = list;
            this.mOriginalValues = registeredUsersList;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public StudentsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StudentsListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enroll_students, parent, false));
        }

        @Override
        public void onBindViewHolder(final StudentsListAdapter.ViewHolder holder, int position) {
            final GroupMember.Data item = list.get(position);
            holder.student_name.setText(item.getName());
            holder.uuid.setText(String.valueOf(item.getUuid()));

            if (item.getImage() != null && item.getImage().equalsIgnoreCase("")) {
                holder.profilepic.setImageResource(R.drawable.ic_user);
            } else {

                if (item.getImage() != null && item.getImage().contains("http")) {
                    if (item.getCameraConfig().equalsIgnoreCase("front")) {
                        try {
                            Picasso.with(mContext)
                                    .load(item.getImage())
                                    .error(R.drawable.ic_user)
                                    .fit()
                                    .rotate(270f)
                                    .transform(new CircleTransform())
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.ic_user)
                                    .into(holder.profilepic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (item.getCameraConfig() != null && item.getCameraConfig().equalsIgnoreCase("back")) {
                        try {
                            Picasso.with(mContext)
                                    .load(item.getImage())
                                    .error(R.drawable.ic_user)
                                    .fit()
                                    .transform(new CircleTransform())
                                    .rotate(90f)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.ic_user)
                                    .into(holder.profilepic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Picasso.with(mContext)
                                    .load(item.getImage())
                                    .error(R.drawable.ic_user)
                                    .fit()
                                    .transform(new CircleTransform())
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.ic_user)
                                    .into(holder.profilepic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } else if (item.getImage() != null && item.getImage().contains("sdcard")) {
                    String st_config = "";
                    if (item.getCameraConfig() != null)
                        st_config = item.getCameraConfig();
                    if (st_config.equalsIgnoreCase("front")) {
                        Picasso.with(getApplicationContext())
                                .load(new File(item.getImage()))
                                .fit()
                                .transform(new CircleTransform())
                                .centerCrop()
                                .error(R.drawable.ic_user)
                                .rotate(270f)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.ic_user)
                                .into(holder.profilepic);
                    } else if (st_config.equalsIgnoreCase("back")) {
                        Picasso.with(getApplicationContext())
                                .load(new File(item.getImage()))
                                .fit()
                                .centerCrop()
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_user)
                                .rotate(90f)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.ic_user)
                                .into(holder.profilepic);
                    } else {
                        Picasso.with(getApplicationContext())
                                .load(new File(item.getImage()))
                                .fit()
                                .centerCrop()
                                .error(R.drawable.ic_user)
                                .transform(new CircleTransform())
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.ic_user)
                                .into(holder.profilepic);
                    }

                } else {
                    if (item.getImage() != null && item.getImage().equalsIgnoreCase("")) {
                        Picasso.with(getApplicationContext())
                                .load(new File(item.getImage()))
                                .fit()
                                .centerCrop()
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_user)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.ic_user)
                                .into(holder.profilepic);
                    } else {
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.ic_user)
                                .fit()
                                .centerCrop()
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_user)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.ic_user)
                                .into(holder.profilepic);
                    }

                }
            }

            if (item.getIsEnrolled() != null && item.getIsEnrolled().equalsIgnoreCase("1")) {
                holder.enroll_status.setImageResource(R.drawable.face_ac);
            } else {
                holder.enroll_status.setImageResource(R.drawable.face_iac);
            }

            holder.rel_main.setOnClickListener(v -> {

                Student_Face_Enroll.counttime = 0;
                Student_Face_Enroll.detected = false;
                preferences.setPref(PrefKeys.UUID, String.valueOf(item.getUuid()));
                Intent intent = new Intent(GroupMemberEnrollmentList_Activity.this, Student_Face_Enroll.class);
                startActivity(intent);

            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView student_name, uuid;
            ImageView enroll_status, profilepic;
            RelativeLayout rel_main;

            public ViewHolder(View itemView) {
                super(itemView);
                uuid = itemView.findViewById(R.id.uuid);
                student_name = itemView.findViewById(R.id.student_name);
                enroll_status = itemView.findViewById(R.id.enroll_status);
                profilepic = itemView.findViewById(R.id.profilepic);
                rel_main = itemView.findViewById(R.id.rel_main);

            }
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
                            if (c.getName().toLowerCase(Locale.ENGLISH).contains(charSequence) || String.valueOf(c.getUuid()).toLowerCase(Locale.ENGLISH).contains(charSequence)) {
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
                    list = (ArrayList<GroupMember.Data>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        section_id = preferences.getPref(PrefKeys.GROUP_ID);
        registerReceiver(receiver, new IntentFilter(
                ResponseReceiver.ACTION_RESP));
        showData();

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(receiver);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

    }


    @Override
    public void onBackPressed() {
        type = 1;
        if (isNetWorkAvailable()) {

            checkLock();
        } else {
            finish();
        }

    }

    public void checkLock() {

        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .checkLock("unlock ", preferences.getPref(PrefKeys.GROUP_ID), RetrofitInstance.getMacAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SuccessModel>() {
                    @Override
                    public void onCompleted() {
                        if (isNetWorkAvailable()) {
                            offline_list_students.clear();
                            offline_list_students = SQLite.select().from(Student_EnroolList_Local.class)
                                    .where(Student_EnroolList_Local_Table.group_id.eq(preferences.getPref(PrefKeys.GROUP_ID)))
                                    .and(Student_EnroolList_Local_Table.isEnrolled.is("1"))
                                    .and(Student_EnroolList_Local_Table.isEnrolledLocal.is("1"))
                                    .queryList();

                            if (offline_list_students.size() > 0) {
                                syncData();
                            } else {
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(SuccessModel successModel) {

                        Log.d(TAG, "onNext: " + successModel.toString());
                    }
                });

    }

}
