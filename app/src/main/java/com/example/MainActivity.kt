package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.firebase.configureAppCheck
import com.example.ui.navigation.NoorRootApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // MUST initialize App Check before any Firebase AI Logic / Firestore calls or ViewModels
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            NoorRootApp()
        }
    }
}
