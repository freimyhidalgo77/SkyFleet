package edu.ucne.skyplanerent.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.HomeScreen
import edu.ucne.skyplanerent.presentation.login.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    NavHost(navController = navController, startDestination = if (auth.currentUser != null) "home" else "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
