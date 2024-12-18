package com.example.test24simpleapp2.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test24simpleapp2.model.ModelClass
import com.example.test24simpleapp2.service.PostRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response

class MyViewModel(private val postRepository: PostRepository) : ViewModel() {
    val data = MutableLiveData<List<ModelClass>>()
    val errorMsg = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchAllData() {
        viewModelScope.launch {
            runCatching {
                try {
                    Log.d("MyViewModel", "try")
                    val allData = mutableListOf<ModelClass>()
                    var id = 1
                    while (true) {
                        val response: Response<ModelClass> = postRepository.getPost(id)
                        if (response.isSuccessful) {
                            response.body()?.let {
                                allData.add(it)
                            } ?: break
                        } else {
                            break
                        }
                        id++
                    }
                    data.value = allData
                } catch (e: HttpException) {
                    Log.d("MyViewModel", "error")
                    e.printStackTrace()
                }
            }.onFailure {
                it.printStackTrace()
                Log.d("MyViewModel", "onFailure")
                isLoading.value = false
            }
        }
    }

    fun fetchData(startId: Int, endId: Int) {
        viewModelScope.launch {
            runCatching {
                try {
                    val allData = mutableListOf<ModelClass>()
                    for (id in startId..endId) {
                        val response: Response<ModelClass> = postRepository.getPost(id)
                        if (response.isSuccessful) {
                            response.body()?.let {
                                allData.add(it)
                            }
                        }
                    }
                    data.value = allData
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.onFailure {
                it.printStackTrace()
                Log.d("MyViewModel", "onFailureDetails")
                isLoading.value = false
            }
        }
    }
}