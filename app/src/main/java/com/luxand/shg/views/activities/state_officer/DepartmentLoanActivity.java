package com.luxand.shg.views.activities.state_officer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.BarChartModel;
import com.luxand.shg.model.VillageOfDashBoard;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DepartmentLoanActivity extends BaseActivity {
    private static final String TAG = "DepartmentLoanActivity";
    MySharedPreference preference;

    BarChart barchart,barchart1;
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<String> labels1 = new ArrayList<>();
    ArrayList<Entry> entries = new ArrayList<>();
    List<BarChartModel.Results> analytics_lists=new ArrayList<>();
    List<VillageOfDashBoard.GroupUsers> members_analytics_list=new ArrayList<>();
    TextView tv_loan_amount_collected,tv_loan_amount_disbursed,tv_barchart_name,tv_title;
    ImageView ivMenuOpen,ivMenuBack;
    List<BarChartModel.Results> dataList;
    BarChartModel.Data data;
    String districtId,mandalId,villageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_loans_department);

        barchart = findViewById(R.id.barchart);
        barchart1 = findViewById(R.id.barchart1);
        tv_loan_amount_collected = findViewById(R.id.tv_loan_amount_collected);
        tv_loan_amount_disbursed = findViewById(R.id.tv_loan_amount_disbursed);
        tv_title = findViewById(R.id.tv_title);
        tv_barchart_name = findViewById(R.id.tv_active_groups_name);
        preference = new MySharedPreference(this);
        ivMenuOpen = findViewById(R.id.iv_menu_open);
        ivMenuBack = findViewById(R.id.iv_menu_back);
        ivMenuOpen.setVisibility(View.GONE);
        ivMenuBack.setVisibility(View.VISIBLE);
        ivMenuBack.setOnClickListener(view -> {
            if(data.getLabel() != null &&data.getLabel().equalsIgnoreCase("group")){
                tv_barchart_name.setText("Village Wise Groups");
                getDashboard(DepartmentLoanActivity.this,districtId,mandalId,"");
                //districtId =String.valueOf(dataList.get((int)e.getX()).getId());

            }else if(data.getLabel() != null && data.getLabel().equalsIgnoreCase("village")){
                tv_barchart_name.setText("Mandal Wise Groups");
                //mandalId = String.valueOf(dataList.get((int)e.getX()).getId());
                getDashboard(DepartmentLoanActivity.this,districtId,"","");
            }else if(data.getLabel() != null && data.getLabel().equalsIgnoreCase("mandal")){
                tv_barchart_name.setText("District Wise Groups");
                //villageId = String.valueOf(dataList.get((int)e.getX()).getId());
                getDashboard(DepartmentLoanActivity.this,"","","");
                //getDashboard(DepartmentActiveGroupsActivity.this,districtId,mandalId,villageId);
            }else{
                finish();
            }
        });

        tv_title.setText("Loan");
        getDashboard(this,"","","");

        barchart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                preference.setPref(PrefKeys.DISTRICT_ID,String.valueOf(dataList.get((int)e.getX()).getId()));
                if(data.getLabel() != null &&data.getLabel().equalsIgnoreCase("district")){
                    tv_barchart_name.setText("District Wise Loans");
                    districtId =String.valueOf(dataList.get((int)e.getX()).getId());
                    getDashboard(DepartmentLoanActivity.this,districtId,"","");
                }else if(data.getLabel() != null && data.getLabel().equalsIgnoreCase("mandal")){
                    tv_barchart_name.setText("Mandal Wise Loans");
                    mandalId = String.valueOf(dataList.get((int)e.getX()).getId());
                    getDashboard(DepartmentLoanActivity.this,districtId,mandalId,"");
                }else if(data.getLabel() != null && data.getLabel().equalsIgnoreCase("village")){
                    tv_barchart_name.setText("Village Wise Loans");
                    villageId = String.valueOf(dataList.get((int)e.getX()).getId());
                    getDashboard(DepartmentLoanActivity.this,districtId,mandalId,villageId);
                }
                //((VillageOfficerMainActivity)this).setHomeFragment(new VillageOfficerMeetingListFragment(),null);
                //Log.d(TAG, "onValueSelected: getDataIndex size "+dataList.size() +dataList.get((int)h.getX()));

            }

            @Override
            public void onNothingSelected() {

            }
        });
        
    }

    private void getDashboard(Context mContext, String district_id, String mandal_id, String village_id) {
        ProgressBarDialog.showLoadingDialog(mContext);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getDepartmentLoans(district_id,mandal_id,village_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BarChartModel>() {

                    @Override
                    public void onNext(BarChartModel barChartModel) {
                        ProgressBarDialog.cancelLoading();
                        if (barChartModel.getSuccess()) {
                            dataList = barChartModel.getData().getResults();
                            data = barChartModel.getData();
                            tv_loan_amount_collected.setText(String.valueOf(data.getCollected()));
                            tv_loan_amount_disbursed.setText(String.valueOf(data.getDisbursed()));
                            try {
                                labels.clear();
                                labels1.clear();
                                entries.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            analytics_lists=barChartModel.getData().getResults();

                            for (int i = 0; i <analytics_lists.size(); i++) {

                                if (labels.size() > 9 && labels.size() < 15) {
                                    barchart.zoom(0, 0, 0, 0);
                                    barchart.zoom(2, 0, 2, 0);
                                } else if (labels.size() > 5 && labels.size() < 10) {
                                    barchart.zoom(0, 0, 0, 0);
                                    barchart.zoom(2, 0, 2, 0);
                                } else {
                                    barchart.zoom(0, 0, 0, 0);
                                }
                            }

                            setBarchart();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }

                });

    }

    private void setBarchart(){
        ArrayList<BarEntry> entries_values = new ArrayList<>();
        ArrayList<BarEntry> entries_values2 = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for(int i=0;i<analytics_lists.size();i++){
            entries_values.add(new BarEntry(i,analytics_lists.get(i).getCollectedAmount()));
            entries_values2.add(new BarEntry(i,analytics_lists.get(i).getDisbursedAmount()));
            labels.add(analytics_lists.get(i).getName());
        }

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();

        BarDataSet dataset = new BarDataSet(entries_values, "");
        dataset.setColor(getResources().getColor(R.color.sky_blue));
        BarDataSet dataset2 = new BarDataSet(entries_values, "");
        dataset.setColor(getResources().getColor(R.color.black));
        //dataset.setValueFormatter(new PercentFormatter());

        barDataSets.add(dataset);

        BarData barData = new BarData(dataset,dataset2);
        barchart.setData(barData);


        //show to xaxis values
        final XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        //xAxis.setCenterAxisLabels(true);
        //xAxis.setLabelRotationAngle(-90);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        //show to yaxis values
        YAxis leftAxis = barchart.getAxisLeft();
        leftAxis.setLabelCount(analytics_lists.size(), false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        YAxis yAxisRight = barchart.getAxisRight();
        yAxisRight.setEnabled(false);

        /*Legend l = barchart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        */
        barchart.getLegend().setEnabled(false);


        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet
        setBarWidth(barData);
        //barData.setBarWidth(barWidth);
        barchart.setDragEnabled(true);
        //barchart.setVisibleXRangeMaximum(3);
        //barchart.groupBars(0,groupSpace,barSpace);
        barchart.setDrawValueAboveBar(true);
        barchart.getDescription().setEnabled(false);
        barchart.setDrawGridBackground(false);
        barchart.invalidate();
    }

    private void setBarWidth(BarData barData) {
        float defaultBarWidth;
        if (barData.getDataSets().size() > 1) {
            float barSpace = 0.02f;
            float groupSpace = 0.3f;
            defaultBarWidth = (1 - groupSpace) / barData.getDataSets().size() - barSpace;
            if (defaultBarWidth >= 0) {
                barData.setBarWidth(defaultBarWidth);
            } else {
                Toast.makeText(this, "Default Barwdith " + defaultBarWidth, Toast.LENGTH_SHORT).show();
            }
            int groupCount = barData.getDataSetCount();
            if (groupCount != -1) {
                barchart.getXAxis().setAxisMinimum(0);
                //barchart.getXAxis().setAxisMaximum(0 + barchart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                barchart.getXAxis().setCenterAxisLabels(true);
            } else {
                Toast.makeText(this, "no of bar groups is " + groupCount, Toast.LENGTH_SHORT).show();
            }

            barchart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
            barchart.invalidate();

        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(data.getLabel() != null &&data.getLabel().equalsIgnoreCase("group")){
            tv_barchart_name.setText("Village Wise Groups");
            getDashboard(DepartmentLoanActivity.this,districtId,mandalId,"");
            //districtId =String.valueOf(dataList.get((int)e.getX()).getId());

        }else if(data.getLabel() != null && data.getLabel().equalsIgnoreCase("village")){
            tv_barchart_name.setText("Mandal Wise Groups");
            //mandalId = String.valueOf(dataList.get((int)e.getX()).getId());
            getDashboard(DepartmentLoanActivity.this,districtId,"","");
        }else if(data.getLabel() != null && data.getLabel().equalsIgnoreCase("mandal")){
            tv_barchart_name.setText("District Wise Groups");
            //villageId = String.valueOf(dataList.get((int)e.getX()).getId());
            getDashboard(DepartmentLoanActivity.this,"","","");
            //getDashboard(DepartmentActiveGroupsActivity.this,districtId,mandalId,villageId);
        }else{
            finish();
        }

    }
}
