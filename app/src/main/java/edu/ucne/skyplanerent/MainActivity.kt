package edu.ucne.skyplanerent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.skyplanerent.presentation.navigation.AppNavigation


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MaterialTheme {
                AppNavigation(context = this)
            }
        }
    }
}

