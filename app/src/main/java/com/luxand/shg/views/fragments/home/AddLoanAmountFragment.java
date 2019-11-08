package com.luxand.shg.views.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.luxand.shg.R;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.AddLoan;
import com.luxand.shg.model.ListData;
import com.luxand.shg.model.SuccessModel;
import com.luxand.shg.util.ProgressBarDialog;
import com.luxand.shg.views.activities.MainActivity;
import com.raizlabs.android.dbflow.annotation.NotNull;


import java.util.List;
import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddLoanAmountFragment extends Fragment {
    private static final String TAG = "AddLoanFragment";
    Spinner spGroupNames;
    EditText etLoanAmount;
    Button btCancel,btSubmit;
    MySharedPreference preference;
    List<AddLoan.Data> dataList;
    int enrollmentId;
    int loanAmount = 521245;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_loan_member,container,false);
        spGroupNames = view.findViewById(R.id.sp_group_select);
        etLoanAmount = view.findViewById(R.id.et_loan_amount);
        btCancel = view.findViewById(R.id.bt_cancel);
        btSubmit = view.findViewById(R.id.bt_submit);
        ((MainActivity)getActivity()).iv_menu_open.setVisibility(View.GONE);
        ((MainActivity)getActivity()).iv_menu_back.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).tv_title.setText("Loans");

        int group_loan_id = getArguments().getInt(PrefKeys.GROUP_LOAN_ID);
        preference = new MySharedPreference(Objects.requireNonNull(getActivity()));
        btSubmit.setOnClickListener(view1 -> {
            int sanctionedLoan = Integer.parseInt(etLoanAmount.getText().toString().trim());
            if(sanctionedLoan  < loanAmount){
                JsonObject object = new JsonObject();
                object.addProperty("enrollmentNo", enrollmentId);
                object.addProperty("group_loan_id", group_loan_id);
                object.addProperty("amount",etLoanAmount.getText().toString().trim());
                sanctionLoan(object);
            }else{
                Toast.makeText(getActivity(), "Entered Amount is More Than Available Amount!", Toast.LENGTH_SHORT).show();
            }

        });
        btCancel.setOnClickListener(view1 -> getFragmentManager().popBackStack());
        getMeetingList();

        spGroupNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                enrollmentId = dataList.get(i).getEnrollmentno();
                Log.d(TAG, "onItemSelected: loanUserId "+ enrollmentId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    private void sanctionLoan(JsonObject object){

        ProgressBarDialog.showLoadingDialog(getActivity());
        RetrofitInstance.getInstance(getActivity())
                .getRestAdapter()
                .addLoan(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SuccessModel>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: sendFeedbackToServer"+e.getMessage() );
                    }

                    @Override
                    public void onNext(SuccessModel meeting) {

                        Log.d(TAG, "onNext: sendFeedbackToServer"+meeting.getRole_id());
                        ProgressBarDialog.cancelLoading();
                        getFragmentManager().popBackStack();

                    }
                });


    }

    private void getMeetingList(){

        ProgressBarDialog.showLoadingDialog(getActivity());
        RetrofitInstance.getInstance(getActivity())
                .getRestAdapter()
                .getMemberList(preference.getPref(PrefKeys.GROUP_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<AddLoan>() {
                    @Override
                    public void onCompleted() {
                        ProgressBarDialog.cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressBarDialog.cancelLoading();
                        Log.e(TAG, "onError: "+e.getMessage());
                    }

                    @Override
                    public void onNext(AddLoan meeting) {
                        ProgressBarDialog.cancelLoading();
                        dataList = meeting.getData();
                        if (dataList != null)
                            spGroupNames.setAdapter(new AddLoanAmountFragment.SpinnerAdapter(getActivity(),R.layout.spinner_view,dataList));
                        spGroupNames.setSelection(0);

                    }
                });

    }

    class SpinnerAdapter extends ArrayAdapter<AddLoan.Data> {
        LayoutInflater inflater;
        int resource;
        SpinnerAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource = resource;

        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            AddLoan.Data data = getItem(position);

            View rowview = inflater.inflate(resource,parent,false);

            TextView txtTitle =  rowview.findViewById(R.id.title);
            assert data != null;
            txtTitle.setText(data.getName());


            return rowview;
        }
    }


}
