package com.example.iotapp.api;

import com.example.iotapp.models.Account;
import com.example.iotapp.models.DeviceDataResult;
import com.example.iotapp.models.LoginResult;
import com.example.iotapp.models.RequestData;
import com.example.iotapp.models.Room;
import com.example.iotapp.models.RoomDevices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.169.54:3000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);


    @POST("auth/login")
    Call<LoginResult> login(@Body Account account);

    @GET("room")
    Call<List<Room>> getAllRooms(@Header("Authorization") String Token);

    @GET("device/room/{roomId}")
    Call<RoomDevices> getDevicesInRoom(@Header("Authorization") String Token, @Path("roomId") String roomId);

    @POST("data")
    Call<DeviceDataResult> getDeviceData(@Header("Authorization") String Token, @Body RequestData requestData);

}
