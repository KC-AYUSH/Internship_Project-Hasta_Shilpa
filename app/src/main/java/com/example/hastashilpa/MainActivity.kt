package com.example.hastashilpa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.hastashilpa.navigation.AppNavigation
import com.example.hastashilpa.ui.theme.HastaShilpaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()        // ← add this line
        enableEdgeToEdge()
        setContent {
            HastaShilpaTheme {
                AppNavigation()
            }
        }
    }
}