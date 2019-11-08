package com.luxand.shg.api;


import android.content.Context;
import android.util.Base64;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.luxand.shg.BuildConfig;
import com.luxand.shg.helper.MySharedPreference;
import com.luxand.shg.helper.PrefKeys;


import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {

    private static RetrofitInstance instance;
    Context mContext;
    private  static String AUTH_URL = "http://mdfrs.com/shg/";
    MySharedPreference  mySharedPreference ;

    public RetrofitInstance(Context mContext){
        this.mContext = mContext;
        mySharedPreference= new MySharedPreference(mContext);
    }
    public static RetrofitInstance getInstance(Context mContext){
       if(instance == null){
           instance = new RetrofitInstance(mContext);
       }
        return instance;
    }

    public Api getRestAdapter(){

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(
                        chain -> {
                            String creds = String.format("%s:%s", "admin", "1234");
                            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                            Request request = chain.request().newBuilder()
                                    .addHeader("Content-Type", "Application/JSON")
                                    .addHeader("Bearer", mySharedPreference.getPref(PrefKeys.ACCESS_TOKEN))
                                    .build();
                            return chain.proceed(request);
                        }).build();


        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        return retrofit.create(Api.class);
    }
    public Api getRestAdapterAuth(){

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {

                                Request request = chain.request().newBuilder()
                                        .addHeader("Content-Type", "Application/JSON")
                                        .build();
                                return chain.proceed(request);
                            }
                        }).build();


        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        return retrofit.create(Api.class);
    }

    public static String getMacAddress() {

        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";

    }
}
