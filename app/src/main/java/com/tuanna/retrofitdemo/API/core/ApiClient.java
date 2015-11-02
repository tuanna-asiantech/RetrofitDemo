package com.tuanna.retrofitdemo.API.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tuanna.retrofitdemo.API.Api;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Project RetrofitDemo.
 * Copyright © 2015.
 * Created by tuanna.
 */
public class ApiClient {

    private static final String HEADER = "User-Agent";

    private static ApiClient sApiClient;

    private Retrofit mRetrofit;
    /**
     * OkHttpClient:by OkHttp
     */
    private OkHttpClient mOkHttpClient;
    /**
     * Api service
     */
    private Api mApi;
    /**
     * android application context
     */
    private Context mContext;

    // Define the interceptor, add authentication headers
    private Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request newRequest = chain.request().newBuilder().addHeader(HEADER, createUserAgent()).build();
            return chain.proceed(newRequest);
        }
    };

    /**
     * get singleton instance
     *
     * @return
     */
    public static synchronized ApiClient getInstance() {
        if (sApiClient == null) {
            sApiClient = new ApiClient();
        }
        return sApiClient;
    }

    public static Api Builder() {
        return getInstance().mApi;
    }

    public void init(String baseUrl) {
        // Add the interceptor to OkHttpClient
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.interceptors().add(mInterceptor);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mOkHttpClient)
                .build();

        mApi = mRetrofit.create(Api.class);
    }

    private String createUserAgent() {
        PackageManager pm = mContext.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return System.getProperty("http.agent") + " " + mContext.getPackageName() + "/" + versionName;
    }
}
