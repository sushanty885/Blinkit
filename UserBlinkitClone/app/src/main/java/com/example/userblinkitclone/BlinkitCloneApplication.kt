package com.example.userblinkitclone

import android.app.Application

class BlinkitCloneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.initialize()
    }

}