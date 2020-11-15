package com.developerth.sodexobooking.services;

import com.developerth.sodexobooking.CONSTANTS;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CONSTANTS.SERVER_IP_ADDRESS)
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static Service getService(){
        Service service = getRetrofit().create(Service.class);
        return service;
    }

}