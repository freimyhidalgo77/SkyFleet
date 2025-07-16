package edu.ucne.skyplanerent.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.HomeScreen
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.presentation.login.FirstScreen
import edu.ucne.skyplanerent.presentation.login.LoginScreen
import edu.ucne.skyplanerent.presentation.login.RegisterScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.RutaScreenDetails
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.Rutas_Viajes_Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

@Composable
fun AppNavigation() {
    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()


    NavHost(
        navController = navController,
        startDestination = Screen.FirstScreen
    ) {

        composable<Screen.FirstScreen> {
            FirstScreen(navController)
        }

        composable<Screen.Home> {
            HomeScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.FirstScreen) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onNavigateToReserva = {
                    navController.navigate(Screen.Reserva)
                },

                onNavigateToRutas_Viajes = {
                    navController.navigate(Screen.Rutas_y_viajes)
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

        composable<Screen.Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login(0)) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                }
            )
        }


        composable<Screen.Reserva> {
            ReservaListScreen(
                scope = scope,
                onCreate = { /* navController.navigate(...) */ },
                onEdit = { /* navController.navigate(...) */ },
                onDelete = { /* lógica */ }
            )
        }

        composable<Screen.Rutas_y_viajes> {
            Rutas_Viajes_Screen (
                scope = scope,
                onCreate = { /* navController.navigate(...) */ },
                onEdit = { /* navController.navigate(...) */ },
                onDelete = { /* lógica */ },
                goBackDetails = {
                    navController.navigate(Screen.RutaDetails(0))
                },
                goToFormulario = {
                    navController.navigate(Screen.Formulario(0))
                }
            )
        }

        composable<Screen.RutaDetails> {
            val args = it.toRoute<Screen.RutaDetails>()
            RutaScreenDetails(
                rutaId = args.rutaId,
                goBack = {
                    navController.navigate(Screen.RutaDetails(0))
                }
            )
        }

        composable<Screen.Formulario> {
            val args = it.toRoute<Screen.Formulario>()
            FormularioScreen (
                formularioId = args.formularioId,
                goBack = {
                    navController.navigate(Screen.Formulario(0))
                }
            )
        }



    }
}
