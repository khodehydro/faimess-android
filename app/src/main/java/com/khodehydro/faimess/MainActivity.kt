package com.khodehydro.faimess

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.khodehydro.faimess.ui.FrindleApp
import com.khodehydro.faimess.ui.theme.FrindleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            // API 26 cannot draw dark navigation icons; use a dark fallback scrim there.
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.rgb(32, 40, 25)),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            FrindleTheme {
                FrindleApp()
            }
        }
    }
}
