package com.luxand.shg.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luxand.shg.BuildConfig;
import com.luxand.shg.views.activities.CreateMeetingActivity;
import com.luxand.shg.views.activities.GroupMemberEnrollmentList_Activity;
import com.luxand.shg.api.Api;
import com.luxand.shg.helper.Helper;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;
import com.luxand.shg.model.Download;
import com.luxand.shg.model.GroupDatfile;
import com.luxand.shg.views.activities.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class DownloadGroupDatFile extends IntentService {
    private static final String TAG = "DownloadGroupDatFile";
    public DownloadGroupDatFile() {
        super("Download Service");
    }

    private NotificationManager notificationManager;
    private int totalFileSize;
    MySharedPreference preference;
    int pos=0;
    String pos_url;
    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        DatDownload();
    }
    public void sendData(int i){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);

            broadcastIntent.putExtra("per", i);
            sendBroadcast(broadcastIntent);

    }

    private void DatDownload(){

        sendData(0);
        final List<GroupDatfile.DatUrl> datUrls= MainActivity.datUrl;

        preference=new MySharedPreference(getApplicationContext());

        Observable.from(datUrls)
                .flatMap(new Func1<GroupDatfile.DatUrl, Observable<GroupDatfile.DatUrl>>() {
                    @Override
                    public Observable<GroupDatfile.DatUrl> call(GroupDatfile.DatUrl datUrl) {
                        pos_url=datUrl.getUrl();
                        initDownload_new(datUrl.getUrl());
                        return Observable.just(datUrl);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GroupDatfile.DatUrl>() {
                    @Override
                    public void onCompleted() {
                        Log.d("onCompletedparent","true");
                        sendData(datUrls.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        for(int i=0;i<datUrls.size();i++)
                        {
                            if (pos_url.equalsIgnoreCase(datUrls.get(i).getUrl()))
                            {
                                pos=i;
                                sendData(pos);

                                break;
                            }
                        }
                    }

                    @Override
                    public void onNext(GroupDatfile.DatUrl integer) {

                        for(int i=0;i<datUrls.size();i++)
                        {
                            if (integer.getUrl().equalsIgnoreCase(datUrls.get(i).getUrl()))
                            {
                                pos=i;
                                sendData(pos);
                                Log.d("onNextparent","onNextparent"+pos+integer.getUrl());

                                break;
                            }
                        }

                    }
                });

    }

    private void initDownload_new(final String url){

      //  sendData(0);
        preference=new MySharedPreference(getApplicationContext());
        List<GroupDatfile.DatUrl> datUrls ;
        if(preference.getPref(PrefKeys.DOWNLOADTYPE).equalsIgnoreCase("attendance")){
            datUrls= MainActivity.datUrl;
        }else{
            datUrls= MainActivity.datUrl;
        }




            if (!url.equalsIgnoreCase("")) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
                        .readTimeout(5, TimeUnit.MINUTES)
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .addInterceptor(interceptor).build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.SERVER_URL + "/")
                        .client(client)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

                Api downloadService = retrofit.create(Api.class);


                downloadService.downloadFile(url)
                        .flatMap(new Func1<Response<ResponseBody>, Observable<Response<ResponseBody>>>() {
                            @Override
                            public Observable<Response<ResponseBody>> call(Response<ResponseBody> responseBodyResponse) {
                                try {
                                    downloadFile(responseBodyResponse.body(), url.substring(url.lastIndexOf('/') + 1, url.length()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return Observable.just(responseBodyResponse);
                            }
                        })
                        .subscribe(new Observer<Response<ResponseBody>>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Log.d("onError","onErrorFile"+pos+pos_url);
                                for(int i=0;i<datUrls.size();i++)
                                {
                                    if (pos_url.equalsIgnoreCase(datUrls.get(i).getUrl()))
                                    {
                                        pos=i;
                                        sendData(pos);

                                        break;
                                    }
                                }
                                Log.d("onError","onError"+pos+pos_url);

                            }

                            @Override
                            public void onNext(Response<ResponseBody> file) {
                                Log.d("onNextparent","File");
                                for(int i=0;i<datUrls.size();i++)
                                {
                                    if (pos_url.equalsIgnoreCase(datUrls.get(i).getUrl()))
                                    {
                                        pos=i;
                                        sendData(pos);
                                        Log.d("onNextparent","onNextparent"+pos+pos_url);

                                        break;
                                    }
                                }
                                Log.d("onNextparent","onNextparent"+pos+pos_url);

                            }
                        });

            }


    }



    private void downloadFile(ResponseBody body, String fileName) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File appDir= new File(Helper.DATFILE+"/"+ Helper.GROUP_DATFILE+"/"+preference.getPref(PrefKeys.GROUP_ID));
        if (!appDir.exists()){
            appDir.mkdirs();
        }

        File outputFile = new File(appDir, fileName);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                //sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        output.flush();
        output.close();
        bis.close();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}