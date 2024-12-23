package com.example.adision;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoodApi {
    @POST("/foods")
    Call<Food> addFood(@Body Food food);

    @GET("/foods")
    Call<List<Food>> getFoodsByCategory(@Query("category") String category);

    @GET("/foods")
    Call<List<Food>> getAllFoods();

    @DELETE("/foods/{id}")
    Call<Void> deleteFood(@Path("id") int id);

    @GET("/foods/{name}")
    Call<Food> getFoodByName(@Path("name") String name);
}
