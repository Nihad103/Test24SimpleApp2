package com.example.test24simpleapp2.service

import com.example.test24simpleapp2.model.ModelClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PostRepository(private val postInterface: PostInterface) {
    suspend fun getPost(id: Int) : Response<ModelClass> {
        return withContext(Dispatchers.IO) {
            postInterface.getData(id)
        }
    }
}