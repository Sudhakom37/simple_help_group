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

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FrKeyViewModel extends AndroidViewModel {
    private static final String TAG = "FrKeyViewModel";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<FrKey> mut_data=new MutableLiveData<>();
    private ViewListener mListener;
    Context mContext;

    public FrKeyViewModel(@NonNull Application application) {
        super(application);
        this.mContext=application;
    }

    public LiveData<FrKey> login(String macAddress){

        Log.d(TAG, "login: object"+macAddress);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getFrKey(macAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FrKey>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(FrKey frKey) {

                        Log.d(TAG, "onNext: "+frKey.getData().getFr_key());
                        mListener.onLoginResponse(frKey);
                    }
                });

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
    public void setViewListener(ViewListener listener) {
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
