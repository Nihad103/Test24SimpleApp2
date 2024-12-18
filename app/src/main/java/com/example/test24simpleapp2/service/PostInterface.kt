package com.example.test24simpleapp2.service

import com.example.test24simpleapp2.model.ModelClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostInterface {

    @GET("posts/{id}")
    suspend fun getData(
        @Path("id")
        id: Int
    ) : Response<ModelClass>
}