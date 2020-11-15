package com.developerth.sodexobooking.services;

import com.developerth.sodexobooking.CONSTANTS;
import com.developerth.sodexobooking.data.model.LoginRequest;
import com.developerth.sodexobooking.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface Service {
    @Headers({
            "Content-Type: application/json",
            "Authorization:"+ CONSTANTS.Authorization,
    })
    @POST(CONSTANTS.URL_LOGIN)
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);
}
