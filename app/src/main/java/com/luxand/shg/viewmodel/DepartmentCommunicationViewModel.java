package com.luxand.shg.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.model.DefaultersModel;
import com.luxand.shg.model.FeedbacksModel;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DepartmentCommunicationViewModel extends AndroidViewModel {

    private static final String TAG = "DepartmentCommunicationViewModel";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<FeedbacksModel> mut_data=new MutableLiveData<>();
    private LoginVewModel.ViewListener mListener;
    Context mContext;
    public DepartmentCommunicationViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<FeedbacksModel> getDepartmentCommunicationData(String group_id){

        Log.d(TAG, "login: object"+group_id);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getDepartmentCommunicationData(group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FeedbacksModel>() {
                    @Override
                    public void onCompleted() {



                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(FeedbacksModel videoModel) {
                        mut_data.postValue(videoModel);
                    }
                });

        return mut_data;
    }

    public LiveData<FeedbacksModel> getData(){
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
