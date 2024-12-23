package com.example.adision;

import androidx.room.Delete;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("/orders")
    Call<Order> getOrderById(@Query("id") int id);

    @PUT("/orders/{id}")
    Call<Order> updateOrder(@Path("id") int id, @Body Order order);
}
