package com.luxand.shg.views.activities.division_offcer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.RootMapModel;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.BaseActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DivisionRootMapActivity extends BaseActivity {
    private static final String TAG = "DivisionRootMapActivity";
    WebView web_root_map;
    TextView tv_title,tvTime,tvMeetingArea,tvMeetingDistance,tvMeetingDate;
    MySharedPreference preference;
    ImageView ivMenuBack,ivMenuOpen;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_map);
        web_root_map = findViewById(R.id.web_root_map);
        tv_title = findViewById(R.id.tv_title);
        tvTime = findViewById(R.id.tv_time);
        tvMeetingArea = findViewById(R.id.tv_meeting_area);
        tvMeetingDistance = findViewById(R.id.tv_distance);
        tvMeetingDate = findViewById(R.id.tv_meeting_date);
        ivMenuBack = findViewById(R.id.iv_menu_back);
        ivMenuOpen = findViewById(R.id.iv_menu_open);
        ivMenuOpen.setVisibility(View.GONE);
        ivMenuBack.setVisibility(View.VISIBLE);
        ivMenuBack.setOnClickListener(view -> finish());
        tv_title.setText("Meeting");
        web_root_map.getSettings().setJavaScriptEnabled(true);
        preference = new MySharedPreference(this);
        loadMap();
    }

    private void loadMap() {
        ProgressBarDialog.showLoadingDialog(this);
        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .loadMap(preference.getPref(PrefKeys.GROUP_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<RootMapModel>() {
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
                    public void onNext(RootMapModel mapModel) {
                        ProgressBarDialog.cancelLoading();
                        if (mapModel != null) {
                            tvMeetingArea.setText(String.format("%s To %s", mapModel.getData().getStartAddress(), mapModel.getData().getEndAddress()));

                            tvTime.setText(String.format("( %s )", mapModel.getData().getTime_taken()));
                            tvMeetingDistance.setText(String.format("%s Kms", mapModel.getData().getDistance()));
                            //tvMeetingDate.setText("Date"+mapModel.getData().get);
                            web_root_map.loadUrl(mapModel.getData().getMapLink());

                        }

                    }
         });
    }
}
