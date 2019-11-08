package com.luxand.shg.views.activities.village_officer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Caste;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.model.Village;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupCreationActivity extends BaseActivity {


    private static final String TAG = "GroupCreationActivity";
    EditText etGroupName,etMobile,et_profession,et_email, etGroupCode,etArea,etGroupLeaderName,etGroupSize;
    Button btCancel,btSubmit;
    Spinner spGender,spCaste,spVillage;
    TextView title,tvDateOfBirth;
    ImageView iv_menu_open;
    MySharedPreference preference;
    String gender,caste,date;
    int casteId,villageId;
    boolean isDateSelected;
    ArrayList<String> casteNames = new ArrayList<>();
    ArrayList<String> villageNames = new ArrayList<>();
    List<Caste.Data> casteList;
    List<Village.Data> villagesList;

    String[] genderStrings = new String[]{
            "male","female","other"
    };
    String[] casteStrings = new String[]{
            "General","BC","SC","ST"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_creation);
        initViews();
        iv_menu_open.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        preference = new MySharedPreference(this);
        getCastes();
        getVillages();
        btCancel.setOnClickListener(view -> finish());
        /*spGender.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_view,genderStrings));
        spGender.setSelection(0);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: "+genderStrings[i]);
                gender = genderStrings[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        tvDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(GroupCreationActivity.this);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        isDateSelected = true;
                        int month = datePicker.getMonth()+1;
                        date = datePicker.getDayOfMonth()+"-"+month+"-"+datePicker.getYear();
                        Log.d(TAG, "onDateSet: "+date);
                        tvDateOfBirth.setText(date);

                    }
                });
                dialog.show();
            }
        });

        spCaste.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: casteNames"+ casteNames.get(i));
                caste = casteNames.get(i);
                casteId = casteList.get(i).getCasteId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }); spVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: casteNames"+ casteNames.get(i));
                villageId = villagesList.get(i).getVillagesId();
                casteId = casteList.get(i).getCasteId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btSubmit.setOnClickListener(view -> {
            if(etGroupName.getText().toString().equalsIgnoreCase("")){

            }else  if(etMobile.getText().toString().equalsIgnoreCase("")){

            }else  if(etMobile.getText().toString().length()<10){

            }else{
                JsonObject object = new JsonObject();
                object.addProperty("group_name", etGroupName.getText().toString());
                object.addProperty("group_code",preference.getPref(PrefKeys.GROUP_ID));
                object.addProperty("village_id",villageId);
                object.addProperty("group_leader_name",etGroupLeaderName.getText().toString());
                object.addProperty("contactnumber",etMobile.getText().toString());
                object.addProperty("area",etArea.getText().toString());
                object.addProperty("caste_id",casteId);
                object.addProperty("dateofbirth",date);
                object.addProperty("profession",et_profession.getText().toString());
                object.addProperty("group_size",Integer.valueOf(etGroupSize.getText().toString()));
                addGroup(object);
            }
        });
        iv_menu_open.setOnClickListener(view -> finish());

    }

    private void addGroup(JsonObject object) {
        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .addGroup(object)
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
                        Log.d(TAG, "onNext: "+successModel.getMsg());
                        //startActivityBase(GroupCreationActivity.this, Student_Face_Enroll.class);
                        finish();
                    }
                });

    }

    private void getCastes(){
        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .getCastes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<Caste>() {
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
                    public void onNext(Caste caste) {
                        ProgressBarDialog.cancelLoading();
                        casteList = caste.getData();
                        casteNames.clear();
                        for (Caste.Data data :caste.getData()){
                            casteNames.add(data.getCastName());
                        }
                        spCaste.setAdapter(new ArrayAdapter<>(GroupCreationActivity.this,R.layout.spinner_view, casteNames));
                        spCaste.setSelection(0);
                    }
                });


    }private void getVillages(){
        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .getVillages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<Village>() {
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
                    public void onNext(Village village) {
                        ProgressBarDialog.cancelLoading();
                        villagesList = village.getData();
                        villageNames.clear();
                        for (Village.Data data :village.getData()){
                            villageNames.add(data.getVillageName());
                        }
                        spVillage.setAdapter(new ArrayAdapter<>(GroupCreationActivity.this,R.layout.spinner_view, villageNames));
                        spVillage.setSelection(0);
                    }
                });


    }

    private void initViews(){
        etGroupName = findViewById(R.id.et_member_name);
        etGroupCode = findViewById(R.id.et_group_code);
        etGroupSize = findViewById(R.id.et_group_size);
        etArea = findViewById(R.id.et_area);
        etGroupLeaderName = findViewById(R.id.et_group_name);
        spVillage = findViewById(R.id.sp_village);
        spCaste= findViewById(R.id.sp_caste);
        etMobile = findViewById(R.id.et_mobile_no);
        tvDateOfBirth = findViewById(R.id.tv_dob);
        btCancel = findViewById(R.id.bt_cancel);
        btSubmit = findViewById(R.id.bt_submit);
        title = findViewById(R.id.tv_title);
        iv_menu_open = findViewById(R.id.iv_menu_open);
        et_profession = findViewById(R.id.et_profession);
        //et_email = findViewById(R.id.et_email);
    }
}
