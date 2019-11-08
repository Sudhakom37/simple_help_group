package com.luxand.shg.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.model.FrKey;
import com.luxand.shg.model.HomeModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "FrKeyViewModel";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<HomeModel> mut_data=new MutableLiveData<>();
    private LoginVewModel.ViewListener mListener;
    Context mContext;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
    }


    public LiveData<HomeModel> getHomeData(String roll_num){

        Log.d(TAG, "login: object"+roll_num);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getHomeItems(roll_num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeModel>() {
                    @Override
                    public void onCompleted() {



                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(HomeModel homeModel) {
                        mut_data.postValue(homeModel);
                    }
                });

        return mut_data;
    }

    public LiveData<HomeModel> getData(){
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
