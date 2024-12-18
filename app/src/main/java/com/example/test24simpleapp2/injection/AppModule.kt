package com.example.test24simpleapp2.injection

import com.example.test24simpleapp2.service.PostInterface
import com.example.test24simpleapp2.service.PostRepository
import com.example.test24simpleapp2.viewModel.MyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostInterface::class.java)
    }
    single { PostRepository(get()) }
    viewModel { MyViewModel(get()) }
}