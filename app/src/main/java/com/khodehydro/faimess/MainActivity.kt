package com.khodehydro.faimess

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.khodehydro.faimess.ui.FaimessApp
import com.khodehydro.faimess.ui.theme.FaimessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FaimessAppContextHolder.appContext = applicationContext
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            // API 26 cannot draw dark navigation icons; use a dark fallback scrim there.
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.rgb(0x22, 0x25, 0x2E)),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            FaimessTheme {
                FaimessApp()
            }
        }
    }
}