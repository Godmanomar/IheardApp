package com.novusvista.omar.iheardapp.ui;

import com.novusvista.omar.iheardapp.Notifications.MyResponse;
import com.novusvista.omar.iheardapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            " your Authorization:key"
    })
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
