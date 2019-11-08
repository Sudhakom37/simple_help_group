package com.luxand.shg.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.model.GroupLoan;
import com.luxand.shg.model.GroupUserLoan;
import com.luxand.shg.util.ProgressBarDialog;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupLonDetailsViewModel extends AndroidViewModel {
    private static final String TAG = "GroupLonDetailsViewModel";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<GroupUserLoan> mut_data=new MutableLiveData<>();
    private LoginVewModel.ViewListener mListener;
    Context mContext;

    public GroupLonDetailsViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
    }


    public LiveData<GroupUserLoan> getGroupLoans(String loan_id,String type){
        //ProgressBarDialog.showLoadingDialog(mContext);
        Log.d(TAG, "getGroupLoans: object"+loan_id);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getGroupLoanUsers(loan_id,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GroupUserLoan>() {
                    @Override
                    public void onCompleted() {



                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        //ProgressBarDialog.cancelLoading();
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(GroupUserLoan userSavings) {
                        Log.d(TAG, "onNext: getMessages"+userSavings.getMessage());
                        //ProgressBarDialog.cancelLoading();
                        mut_data.postValue(userSavings);
                    }
                });

        return mut_data;
    }


    public LiveData<GroupUserLoan> getData(){
        return mut_data;
    }

    public void progress_Dialog(boolean status) {
        isLoading.postValue(status);
    }

    public void Progress_Start() {
        progress_Dialog(true);
    }

    public void Progress_Stop() {
        progress_Dialog(false);
    }
}
