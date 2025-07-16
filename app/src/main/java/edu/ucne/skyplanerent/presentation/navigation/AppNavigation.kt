package edu.ucne.skyplanerent.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.HomeScreen
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.login.FirstScreen
import edu.ucne.skyplanerent.presentation.login.LoginScreen
import edu.ucne.skyplanerent.presentation.login.RegisterScreen
import edu.ucne.skyplanerent.presentation.reserva.PagoReservaListScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaDeleteScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaDetailsScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaEditScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.PreReservaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.RutaScreenDetails
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.Rutas_Viajes_Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioScreen

@Composable
fun AppNavigation() {
    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val rutaList by remember { mutableStateOf(emptyList<RutaEntity>()) }
    val tipoList by remember { mutableStateOf(emptyList<TipoVueloEntity>()) }



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
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
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
                onDetails = {navController.navigate(Screen.ReservaDetails(0))},
                onEdit = { navController.navigate(Screen.ReservaEdit(0)) },
                onDelete = {navController.navigate(Screen.ReservaDelete(0)) }
            )
        }

        composable<Screen.Rutas_y_viajes> {
            Rutas_Viajes_Screen(
                scope = scope,
                onCreate = { /* navController.navigate(...) */ },
                onEdit = { /* navController.navigate(...) */ },
                onDelete = { /* lógica */ },
                goBackDetails = {
                    navController.navigate(Screen.RutaDetails(0))
                },
                goTopreReserva = {
                    navController.navigate(Screen.PreReserva(0))
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

        composable<Screen.PreReserva> {
            val args = it.toRoute<Screen.PreReserva>()
            PreReservaListScreen (
                preReservaId = args.prereservaId,
                goBack = {
                    navController.navigate(Screen.PreReserva(0))
                },
                goToFormulario = {
                    navController.navigate(Screen.Formulario(0))
                },
                tipoVueloList = tipoList,
                rutaList = rutaList
            )
        }


        composable<Screen.Formulario> {
            val args = it.toRoute<Screen.Formulario>()
            FormularioScreen(
                formularioId = args.formularioId,
                goBack = {
                    navController.navigate(Screen.Formulario(0))
                },
                goToPago = {
                    navController.navigate(Screen.PagoReserva(0))
                }
            )
        }


        composable<Screen.ReservaDetails> {
            val args = it.toRoute<Screen.ReservaDetails>()
            ReservaDetailsScreen (
                reservaId = args.reservaId,
                goBack = {
                    navController.navigate(Screen.ReservaDetails(0))
                },
                scope = scope,
                goToEdit = {
                    navController.navigate(Screen.ReservaEdit(0))
                },

                goToDelete = {
                    navController.navigate(Screen.ReservaDelete(0))
                }

            )

        }


        composable<Screen.ReservaEdit> {
            val args = it.toRoute<Screen.ReservaEdit>()
            ReservaEditScreen (
                reservaId = args.reservaId,
                goBack = {
                    navController.navigate(Screen.ReservaEdit(0))
                }

            )
        }


        composable<Screen.ReservaDelete> {
            val args = it.toRoute<Screen.ReservaDelete>()
            ReservaDeleteScreen(
                reservaId = args.reservaId,
                goBack = {
                    navController.navigate(Screen.ReservaDelete(0))
                }

            )

        }



            composable<Screen.PagoReserva> {
            val args = it.toRoute<Screen.PagoReserva>()
            PagoReservaListScreen (
                pagoReservaId = args.pagoReservaId,
                goBack = {
                    navController.navigate(Screen.Reserva)
                },
                rutaList = rutaList,
                tipoVueloList = tipoList

            )
        }
    }

}
