package com.example.noteapp

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class MyApp : Application(), ViewModelStoreOwner {

    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

}