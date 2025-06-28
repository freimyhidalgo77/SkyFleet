package edu.ucne.skyplanerent.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.HomeScreen
import edu.ucne.skyplanerent.presentation.login.FirstScreen
import edu.ucne.skyplanerent.presentation.login.LoginScreen

@Composable
fun AppNavigation() {
    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.FirstScreen
    ) {

        composable<Screen.FirstScreen> {
            FirstScreen(navController)
        }
        composable<Screen.Home> {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.FirstScreen) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home)
                }
            )
        }
    }
}
