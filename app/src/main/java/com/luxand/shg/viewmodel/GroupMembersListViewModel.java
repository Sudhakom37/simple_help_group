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
import com.luxand.shg.model.GroupMember;
import com.luxand.shg.model.HomeModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupMembersListViewModel extends AndroidViewModel {

    private static final String TAG = "GroupMembersListViewMod";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<GroupMember> mut_data=new MutableLiveData<>();
    private LoginVewModel.ViewListener mListener;
    Context mContext;
    public GroupMembersListViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
    }
    public LiveData<GroupMember> getGroupMemberList(String group_id){

        Log.d(TAG, "login: object"+group_id);
        mut_data.postValue(null);
        RetrofitInstance.getInstance(mContext).getRestAdapter()
                .getGroupMemberList(group_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GroupMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progress_Dialog(false);
                    }

                    @Override
                    public void onNext(GroupMember groupMember) {
                        mut_data.postValue(groupMember);

                        progress_Dialog(false);
                    }
                });

        return mut_data;
    }

    public LiveData<GroupMember> getData(){
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
