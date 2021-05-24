package com.kz.anreadx.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.kodein.di.android.x.androidXModule
import org.kodein.di.compose.withDI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            withDI(androidXModule(application)) {
                Router()
            }
        }
    }
}
