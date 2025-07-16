package edu.ucne.skyplanerent.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.HomeScreen
import edu.ucne.skyplanerent.presentation.admin.AdminPanelScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveListScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveListScreen
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveScreen
import edu.ucne.skyplanerent.presentation.login.FirstScreen
import edu.ucne.skyplanerent.presentation.login.LoginScreen
import edu.ucne.skyplanerent.presentation.login.RegisterScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.RutaScreenDetails
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.Rutas_Viajes_Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveDetailsScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaDetailsScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloDetailsScreen

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
            Rutas_Viajes_Screen(
                scope = scope,
                onCreate = { /* navController.navigate(...) */ },
                onEdit = { /* navController.navigate(...) */ },
                onDelete = { /* lógica */ },
                goBackDetails = {
                    navController.navigate(Screen.RutaDetails(0))
                }
            )
        }

        composable<Screen.RutaDetails> {
            val args = it.toRoute<Screen.RutaDetails>()
            RutaScreenDetails(
                rutaId = args.rutaId,
                goBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.RutaList> {
            RutaListScreen(
                goToRuta = { id ->
                    navController.navigate(Screen.RutaDetailsScreen(id))
                },
                createRuta = {
                    navController.navigate(Screen.Ruta(null))
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Ruta> { backStack ->
            RutaScreen(
                rutaId = backStack.toRoute<Screen.Ruta>().rutaId,
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.TipoVueloList> {
            TipoVueloListScreen(
                goToTipoVuelo = { id ->
                    navController.navigate(Screen.TipoVueloDetails(id))
                },
                createTipoVuelo = {
                    navController.navigate(Screen.TipoVuelo(null))
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.TipoVuelo> { backStack ->
            TipoVueloScreen(
                tipoVueloId = backStack.toRoute<Screen.TipoVuelo>().tipovueloId,
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.CategoriaAeronaveList> {
            CategoriaAeronaveListScreen(
                goToCategoria = { categoriaId ->
                    navController.navigate("aeronaveList?categoriaId=$categoriaId") // Ruta explícita
                },
                createCategoria = {
                    navController.navigate(Screen.CategoriaAeronave(null))
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.CategoriaAeronave> { backStack ->
            CategoriaAeronaveScreen(
                categoriaId = backStack.toRoute<Screen.CategoriaAeronave>().categoriaId,
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.AdminPanel> {
            AdminPanelScreen(
                navController = navController,
                goBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "aeronaveList?categoriaId={categoriaId}",
            arguments = listOf(
                navArgument("categoriaId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getInt("categoriaId") ?: -1
            val viewModel: AeronaveViewModel = hiltViewModel()
            LaunchedEffect(categoriaId) {
                if (categoriaId != -1) viewModel.filterAeronavesByCategoria(categoriaId)
            }
            AeronaveListScreen(
                goToAeronave = { id ->
                    navController.navigate(Screen.AeronaveDetailsScreen(id)) // Corregido a AeronaveDetails
                },
                createAeronave = {
                    navController.navigate(Screen.Aeronave(null))
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Aeronave> { backStack ->
            AeronaveScreen(
                aeronaveId = backStack.toRoute<Screen.Aeronave>().aeronaveId,
                goBack = { navController.popBackStack() }
            )
        }

        // Nueva definición para AeronaveDetails
        composable<Screen.AeronaveDetailsScreen> { backStack ->
            val args = backStack.toRoute<Screen.AeronaveDetailsScreen>()
            AeronaveDetailsScreen(
                aeronaveId = args.aeronaveId,
                goBack = { navController.popBackStack() },
                onDelete = { id -> /* Lógica para eliminar */ },
                onEdit = { id -> navController.navigate(Screen.Aeronave(id)) }
            )
        }

        // Nueva definición para RutaDetails
        composable<Screen.RutaDetailsScreen> { backStack ->
            val args = backStack.toRoute<Screen.RutaDetailsScreen>()
            RutaDetailsScreen(
                rutaId = args.rutaId,
                goBack = { navController.popBackStack() },
                onDelete = { id -> /* Lógica para eliminar */ },
                onEdit = { id -> navController.navigate(Screen.Ruta(id)) }
            )
        }

        // Nueva definición para TipoVueloDetails
        composable<Screen.TipoVueloDetails> { backStack ->
            val args = backStack.toRoute<Screen.TipoVueloDetails>()
            TipoVueloDetailsScreen(
                tipoVueloId = args.tipovueloId,
                goBack = { navController.popBackStack() },
                onDelete = { id -> /* Lógica para eliminar */ },
                onEdit = { id -> navController.navigate(Screen.TipoVuelo(id)) }
            )
        }
    }
}

