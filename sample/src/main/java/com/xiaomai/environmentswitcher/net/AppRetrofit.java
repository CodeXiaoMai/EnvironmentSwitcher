package com.xiaomai.environmentswitcher.net;

import android.app.Application;

import com.xiaomai.environmentswitcher.BuildConfig;
import com.xiaomai.environmentswitcher.EnvironmentSwitcher;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppRetrofit {

    private static Retrofit sMusicRetrofit;

    private static String sMusicHost = "";

    public static Retrofit getMusicRetrofit(Application context) {
        String host = EnvironmentSwitcher.getMusicEnvironment(context, BuildConfig.DEBUG);
        if (sMusicRetrofit == null || sMusicHost == null || !sMusicHost.equals((host))) {
            sMusicHost = host;
            sMusicRetrofit = new Retrofit.Builder()
                    .baseUrl(sMusicHost)
                    .client(new OkHttpClient.Builder()
                            .build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sMusicRetrofit;
    }

    private static Retrofit sAppRetrofit;

    private static String sAppHost = "";

    public static Retrofit getAppRetrofit(Application context) {
        String host = EnvironmentSwitcher.getAppEnvironment(context, BuildConfig.DEBUG);
        if (sAppRetrofit == null || sAppHost == null || !sAppHost.equals((host))) {
            sAppHost = host;
            sAppRetrofit = new Retrofit.Builder()
                    .baseUrl(sAppHost)
                    .client(new OkHttpClient.Builder()
                            .build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sAppRetrofit;
    }
}
