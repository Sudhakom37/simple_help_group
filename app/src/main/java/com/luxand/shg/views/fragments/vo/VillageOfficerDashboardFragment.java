package com.luxand.shg.views.fragments.vo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.luxand.shg.views.activities.village_officer.VillageOfficerMainActivity;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.VillageOfDashBoard;
import com.luxand.shg.util.ProgressBarDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VillageOfficerDashboardFragment extends Fragment {

    private static final String TAG = "VillageOfficerDashboard";
    MySharedPreference preference;

    BarChart barchart,barchart1;
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<String> labels1 = new ArrayList<>();
    ArrayList<Entry> entries = new ArrayList<>();
    List<VillageOfDashBoard.GroupMeetings> analytics_lists=new ArrayList<>();
    List<VillageOfDashBoard.GroupUsers> members_analytics_list=new ArrayList<>();

    List<VillageOfDashBoard.GroupMeetings> dataList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_village_officer, container, false);
        barchart = view.findViewById(R.id.barchart);
        barchart1 = view.findViewById(R.id.barchart1);
        preference = new MySharedPreference(getActivity());
        ((VillageOfficerMainActivity)getActivity()).tv_title.setText("Dashboard");
       getDashboard(getActivity());

       barchart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
           @Override
           public void onValueSelected(Entry e, Highlight h) {
                preference.setPref(PrefKeys.GROUP_ID,dataList.get((int)e.getX()).getGroup_id());
               ((VillageOfficerMainActivity)getActivity()).setHomeFragment(new VillageOfficerMeetingListFragment(),null);
               Log.d(TAG, "onValueSelected: getDataIndex size "+dataList.size() +dataList.get((int)h.getX()));

           }

           @Override
           public void onNothingSelected() {

           }
       });
        return view;
    }

    private void getDashboard(Context mContext) {
        ProgressBarDialog.showLoadingDialog(mContext);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getVillageOfficerDashboard("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VillageOfDashBoard>() {

                    @Override
                    public void onNext(VillageOfDashBoard data) {
                        ProgressBarDialog.cancelLoading();
                        if (data.getSuccess()) {
                            dataList = data.getData().getGroupMeetings();
                            try {
                                labels.clear();
                                labels1.clear();
                                entries.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            analytics_lists=data.getData().getGroupMeetings();

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
                            members_analytics_list=data.getData().getGroupUsers();
                            for (int i = 0; i <members_analytics_list.size(); i++) {

                                if (labels1.size() > 9 && labels1.size() < 15) {
                                    barchart1.zoom(0, 0, 0, 0);
                                    barchart1.zoom(2, 0, 2, 0);
                                } else if (labels.size() > 5 && labels.size() < 10) {
                                    barchart1.zoom(0, 0, 0, 0);
                                    barchart1.zoom(2, 0, 2, 0);
                                } else {
                                    barchart1.zoom(0, 0, 0, 0);
                                }
                            }
                            setMembersBarchart();
                        } else {

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
        ArrayList<String> labels = new ArrayList<>();

        for(int i=0;i<analytics_lists.size();i++){
            entries_values.add(new BarEntry(i,analytics_lists.get(i).getCount()));
            labels.add(analytics_lists.get(i).getGroupName());
        }

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();

        BarDataSet dataset = new BarDataSet(entries_values, "");
        dataset.setColor(getResources().getColor(R.color.sky_blue));
        //dataset.setValueFormatter(new PercentFormatter());

        barDataSets.add(dataset);

        BarData barData = new BarData(dataset);
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
        leftAxis.setLabelCount(8, false);
        leftAxis.setAxisMaximum(10f);
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
        barData.setBarWidth(barWidth);
        barchart.setDragEnabled(true);
        barchart.setVisibleXRangeMaximum(3);
        //barchart.groupBars(0,groupSpace,barSpace);
        barchart.setDrawValueAboveBar(true);
        barchart.getDescription().setEnabled(false);
        barchart.setDrawGridBackground(false);
        barchart.invalidate();
    }
    private void setMembersBarchart(){
        ArrayList<BarEntry> entries_values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for(int i=0;i<members_analytics_list.size();i++){
            Log.d(TAG, "setMembersBarchart: "+members_analytics_list.get(i).getTotal());
            entries_values.add(new BarEntry(i,members_analytics_list.get(i).getTotal()));
            labels.add(members_analytics_list.get(i).getGroupName());
        }

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();

        BarDataSet dataset = new BarDataSet(entries_values, "");
        dataset.setColor(getResources().getColor(R.color.orange));
        //dataset.setValueFormatter(new PercentFormatter());

        barDataSets.add(dataset);

        BarData barData = new BarData(dataset);
        barchart1.setData(barData);


        //show to xaxis values
        final XAxis xAxis = barchart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        //xAxis.setCenterAxisLabels(true);
        //xAxis.setLabelRotationAngle(-90);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        //show to yaxis values
        YAxis leftAxis = barchart1.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yAxisRight = barchart1.getAxisRight();
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
        barchart1.getLegend().setEnabled(false);


        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet
        barData.setBarWidth(barWidth);
        barchart1.setDragEnabled(true);
        barchart1.setVisibleXRangeMaximum(3);
        //barchart.groupBars(0,groupSpace,barSpace);
        barchart1.setDrawValueAboveBar(true);
        barchart1.getDescription().setEnabled(false);
        barchart1.setDrawGridBackground(false);
        barchart1.invalidate();
    }
    
}
