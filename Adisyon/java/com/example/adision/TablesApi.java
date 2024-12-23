package com.example.adision;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TablesApi {

    @PUT("/tables/{id}")
    Call<Tables> updateTables(@Path("id") int id, @Body Tables tables);

    @GET("/tables")
    Call<List<Tables>> getAll();

    @GET("/tables/{id}")
    Call<Tables> getById(@Path("id") int id);
}
