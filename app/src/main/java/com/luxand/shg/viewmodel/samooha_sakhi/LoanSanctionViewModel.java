package com.luxand.shg.viewmodel.samooha_sakhi;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.luxand.shg.api.RetrofitInstance;
import com.luxand.shg.model.FrKey;
import com.luxand.shg.model.SanctionLoanModel;
import com.luxand.shg.model.SavingsModel;
import com.luxand.shg.viewmodel.LoginVewModel;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoanSanctionViewModel extends AndroidViewModel {


    private static final String TAG = "FrKeyViewModel";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<SanctionLoanModel> mut_data=new MutableLiveData<>();
    private LoginVewModel.ViewListener mListener;
    Context mContext;

    public LoanSanctionViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
    }
    public LiveData<SanctionLoanModel> getSavingsData(String user_id){

        Log.d(TAG, "login: object"+user_id);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getSanctionAmount(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SanctionLoanModel>() {
                    @Override
                    public void onCompleted() {



                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(SanctionLoanModel homeModel) {
                        mut_data.postValue(homeModel);
                    }
                });

        return mut_data;
    }

    public LiveData<SanctionLoanModel> getData(){
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
