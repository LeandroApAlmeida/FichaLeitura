package br.com.leandro.fichaleitura.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application() {


    companion object {

        private lateinit var instance: br.com.leandro.fichaleitura.android.Application

        fun getInstance(): br.com.leandro.fichaleitura.android.Application = instance

    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}