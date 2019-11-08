package com.luxand.shg.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.model.FrKey;
import com.luxand.shg.model.SavingsModel;
import com.luxand.shg.model.UserSavings;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SavingsViewModel extends AndroidViewModel {

    private static final String TAG = "FrKeyViewModel";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<UserSavings> mut_data=new MutableLiveData<>();
    private LoginVewModel.ViewListener mListener;
    Context mContext;
    public SavingsViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
    }


    public LiveData<UserSavings> getSavingsData(String group_id){

        Log.d(TAG, "login: object"+group_id);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getGroupSavings(group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserSavings>() {
                    @Override
                    public void onCompleted() {



                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(UserSavings homeModel) {
                        mut_data.postValue(homeModel);
                    }
                });

        return mut_data;
    }

    public LiveData<UserSavings> getData(){
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

    /*
     * view click listener
     * */
    public void setViewListener(LoginVewModel.ViewListener listener) {
        this.mListener = listener;
    }

    public interface ViewListener {

        void onLoginResponse(FrKey key);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
