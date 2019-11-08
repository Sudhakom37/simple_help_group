package com.luxand.shg.views.activities;

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
import com.luxand.shg.model.Caste;
import com.luxand.shg.views.enrollment.Student_Face_Enroll;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.util.ProgressBarDialog;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddGroupMemberActivity extends BaseActivity{

    private static final String TAG = "AddGroupMemberActivity";
    EditText etName,etMobile,et_profession,et_email, etVillage;
    Button btCancel,btSubmit;
    Spinner spGender,spCaste;
    TextView title,tvDateOfBirth;
    ImageView iv_menu_open;
    MySharedPreference preference;
    String gender,caste;
    int casteId;
    boolean isDateSelected;
    ArrayList<String> casteNames = new ArrayList<>();
    List<Caste.Data> casteList;
    String[] genderStrings = new String[]{
            "male","female","other"
    };
    String[] casteStrings = new String[]{
            "General","BC","SC","ST"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        initViews();
        iv_menu_open.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        preference = new MySharedPreference(this);
        title.setText("Enrollment");
        getCastes();
        btCancel.setOnClickListener(view -> finish());
        spGender.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_view,genderStrings));
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
        });

        tvDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(AddGroupMemberActivity.this);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        isDateSelected = true;
                        int month = datePicker.getMonth()+1;
                        String date = datePicker.getDayOfMonth()+"-"+month+"-"+datePicker.getYear();
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
        });
        btSubmit.setOnClickListener(view -> {
            if(etName.getText().toString().equalsIgnoreCase("")){

            }else  if(etMobile.getText().toString().equalsIgnoreCase("")){

            }else  if(etMobile.getText().toString().length()<10){

            }else{
                JsonObject object = new JsonObject();
                object.addProperty("name",etName.getText().toString());
                object.addProperty("group_id",preference.getPref(PrefKeys.GROUP_ID));
                object.addProperty("gender",gender);
                object.addProperty("dateofbirth",tvDateOfBirth.getText().toString());
                object.addProperty("caste_id",casteId);
                object.addProperty("mobileno",etMobile.getText().toString());
                object.addProperty("profession",et_profession.getText().toString());

                addMember(object);
            }
        });
        iv_menu_open.setOnClickListener(view -> finish());

    }

    private void addMember(JsonObject object) {
        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .addGroupMember(object)
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
                        startActivityBase(AddGroupMemberActivity.this, Student_Face_Enroll.class);
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
                        public void onNext(Caste village) {
                            ProgressBarDialog.cancelLoading();
                            casteList = village.getData();
                            casteNames.clear();
                            for (Caste.Data data :village.getData()){
                                casteNames.add(data.getCastName());
                            }
                            spCaste.setAdapter(new ArrayAdapter<>(AddGroupMemberActivity.this,R.layout.spinner_view, casteNames));
                            spCaste.setSelection(0);
                        }
                    });


    }

    private void initViews(){
        etName = findViewById(R.id.et_member_name);
        spGender = findViewById(R.id.sp_gender);
        spCaste = findViewById(R.id.sp_caste);
        etMobile = findViewById(R.id.et_mobile_no);
        tvDateOfBirth = findViewById(R.id.tv_dob);
        btCancel = findViewById(R.id.bt_cancel);
        btSubmit = findViewById(R.id.bt_submit);
        title = findViewById(R.id.tv_title);
        iv_menu_open = findViewById(R.id.iv_menu_open);
        et_profession = findViewById(R.id.et_profession);
        et_email = findViewById(R.id.et_email);
    }
}
