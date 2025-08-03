package edu.ucne.skyplanerent.presentation.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.HomeScreen
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.admin.AdminPanelScreen
import edu.ucne.skyplanerent.presentation.admin.PerfilAdminScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveListScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveListScreen
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveScreen
import edu.ucne.skyplanerent.presentation.login.FirstScreen
import edu.ucne.skyplanerent.presentation.login.LoginScreen
import edu.ucne.skyplanerent.presentation.login.RegisterScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.Rutas_Viajes_Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveDetailsScreen
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveEvent
import edu.ucne.skyplanerent.presentation.aeronave.TipoAeronaveDetailsScreen
import edu.ucne.skyplanerent.presentation.aeronave.TipoAeronaveListScreen
//import edu.ucne.skyplanerent.presentation.aeronave.CategoriaReservaAeronaveScreen
//import edu.ucne.skyplanerent.presentation.aeronave.TipoAeronaveScreen
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaReservaAeronaveScreen
import edu.ucne.skyplanerent.presentation.login.PerfilClientScreen
import edu.ucne.skyplanerent.presentation.login.ProfileViewModel
import edu.ucne.skyplanerent.presentation.login.SessionManager
import edu.ucne.skyplanerent.presentation.reserva.PagoReservaListScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaDeleteScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaDetailsScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaEditScreen
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.PreReservaListScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioScreen
//import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaDetailsScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaEvent
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaScreenDetails
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloDetailsScreen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloEvent
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.delay
import edu.ucne.skyplanerent.data.remote.dto.AdminDTO
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.presentation.admin.AdminViewModel


@SuppressLint("RememberReturnType", "WrongNavigateRouteType")
@Composable
fun AppNavigation(context: Context) {
    val scope = rememberCoroutineScope()
    val rutaList by remember { mutableStateOf(emptyList<RutaEntity>()) }
    val tipoList by remember { mutableStateOf(emptyList<TipoVueloEntity>()) }

    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()
    val sessionManager = remember { SessionManager(context) }

    var authInitialized by remember { mutableStateOf(false) }

    // Efecto para verificar el estado de autenticación

    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                sessionManager.saveAuthState(user)
            }
            authInitialized = true
        }
        // Timeout para evitar bloqueos
        delay(2000)
        authInitialized = true
    }

    // Verifica tanto FirebaseAuth como SharedPreferences
    val isLoggedIn = auth.currentUser != null || sessionManager.isLoggedIn()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home else Screen.FirstScreen
    ) {
        composable<Screen.FirstScreen> {
            FirstScreen(navController)
        }

        composable<Screen.Home> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val currentUserEmail = auth.currentUser?.email ?: sessionManager.getCurrentUserId()
            HomeScreen(
                navController = navController,
                onLogout = {
                    auth.signOut()
                    sessionManager.clearSession() // Limpia la sesión
                    navController.navigate(Screen.FirstScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToReserva = {
                    navController.navigate(Screen.Reserva)
                },
                onNavigateToRutas_Viajes = {
                    navController.navigate(Screen.Rutas_y_viajes)
                },
                onNavigateToPeril = {
                    navController.navigate(Screen.Perfil)
                },
                currentUserEmail = currentUserEmail,
                userRepository = profileViewModel.userRepository

            )
        }


        composable<Screen.Perfil> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val currentUserEmail = auth.currentUser?.email ?: sessionManager.getCurrentUserId()

            LaunchedEffect(currentUserEmail) {
                profileViewModel.loadUser(currentUserEmail)
            }

            PerfilClientScreen(
                navController = navController,
                goToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                goToFirstScreen = {
                    navController.navigate(Screen.Home)
                },
                onLogout = {
                    auth.signOut()
                    sessionManager.clearSession()
                    navController.navigate(Screen.FirstScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                goBack = { navController.popBackStack() },
                currentUserEmail = currentUserEmail,
                userRepository = profileViewModel.userRepository
            )
        }
        composable<Screen.Login> {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            val adminViewModel: AdminViewModel = hiltViewModel() // Inyecta el ViewModel para obtener adminRepository
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                },
                goToAdminPanel = { adminId ->
                    navController.navigate(Screen.AdminPanel(adminId))
                },
                sessionManager = sessionManager,
                adminRepository = adminViewModel.adminRepository // Pasa el repositorio desde el ViewModel
            )
        }

        composable<Screen.Register> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                },
                userRepository = profileViewModel.userRepository
            )
        }


        composable<Screen.Reserva> {
            ReservaListScreen(
                scope = scope,
                onCreate = { /* navController.navigate(...) */ },
                onDetails = {reserva-> navController.navigate(Screen.ReservaDetails(reserva)) },
                onEdit = {id-> navController.navigate(Screen.ReservaEdit(id, id)) },
                onDelete = { navController.navigate(Screen.ReservaDelete(0)) },
                navController = navController
            )
        }

        composable<Screen.Rutas_y_viajes> { backStackEntry ->
            val reservaViewModel: ReservaViewModel = hiltViewModel(backStackEntry)
            Rutas_Viajes_Screen(
                goToRuta = { id ->
                    navController.navigate(Screen.ReservaRutaDetails(id))
                },
                goBackDetails = {
                    navController.navigate(Screen.RutaDetails(0))
                },
                goTopreReserva = {id->
                    navController.navigate(Screen.PreReserva(id))
                },
                scope = scope,
                reservaViewModel = reservaViewModel,
                goBack = {
                    navController.navigate(Screen.Home)
                },
            )
        }

        /* composable<Screen.ReservaRutaDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ReservaRutaDetails>()
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ReservaRutaDetails)//Aqui tambien puede ir Ruta_Viajes
            }
            val rutaViewModel: RutaViewModel = hiltViewModel(parentEntry)

            ReservaRutaScreenDetails(
                rutaId = args.reservaRutId,
                viewModel = rutaViewModel,
                onSelectRuta = {
                    rutaViewModel.seleccionarRuta(0)
                },
                goBack = {
                    navController.navigate(Screen.Rutas_y_viajes)
                }
            )
        }*/



        composable<Screen.RutaDetails> {
            val args = it.toRoute<Screen.RutaDetails>()
            RutaScreenDetails(
                rutaId = args.rutaId,
                goBack = {
                    navController.navigate(Screen.RutaDetails(0))
                },
                onEdit = {},
                onDelete = {

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
                goToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Ruta> { backStack ->
            val args = backStack.toRoute<Screen.Ruta>()
            RutaScreen(
                rutaId = args.rutaId,
                goBack = { navController.popBackStack() }
            )
        }

        /*composable<Screen.RutaList> {
            RutaListScreen(
                goToRuta = { id ->
                    navController.navigate(Screen.RutaDetailsScreen(id))
                },
                createRuta = {
                    navController.navigate(Screen.Ruta(null))
                },
                goBack = { navController.popBackStack() }
            )

        }*/


        composable<Screen.PreReserva> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Rutas_y_viajes)
            }

            val reservaViewModel: ReservaViewModel = hiltViewModel(parentEntry)

            val args = backStackEntry.toRoute<Screen.PreReserva>()
            PreReservaListScreen(
                preReservaId = args.prereservaId,
                goBack = {
                    navController.navigate(Screen.Rutas_y_viajes)
                },
                goToFormulario = {id->
                    navController.navigate(Screen.Formulario(0,id))
                },
                tipoVueloList = tipoList,
                rutaList = rutaList,
                viewModel = reservaViewModel

            )
        }


       /* composable<Screen.Formulario> {
            val args = it.toRoute<Screen.Formulario>()
            FormularioScreen(
                formularioId = args.formularioId,
                goBack = {id->
                    navController.navigate(Screen.Formulario(id))
                },
                goToPago = { pagoId ->
                    navController.navigate(Screen.PagoReserva(pagoId))
                },
                aeronaveSeleccionadaId = args.formularioId

            )
        }*/

        composable<Screen.Formulario> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.Formulario>()
            FormularioScreen(
                formularioId = args.formularioId,
                aeronaveSeleccionadaId = args.aeronaveId,
                goBack = { navController.popBackStack() },
                goToPago = { pagoId ->
                    navController.navigate(Screen.PagoReserva(pagoId))
                }
            )
        }


        composable<Screen.ReservaDetails> {
            val args = it.toRoute<Screen.ReservaDetails>()
            ReservaDetailsScreen(
                reservaId = args.reservaId,
                goBack = {
                    navController.navigate(Screen.Reserva)
                },
                scope = scope,
                goToEdit = {reservaId->
                    navController.navigate(Screen.ReservaEdit(reservaId,reservaId))
                },

                goToDelete = {reservaId->
                    navController.navigate(Screen.ReservaDelete(reservaId))
                }

            )

        }


        composable<Screen.ReservaEdit> {
            val args = it.toRoute<Screen.ReservaEdit>()
            ReservaEditScreen(
                reservaId = args.reservaId,
                goBack = {reservaId->
                    navController.navigate(Screen.ReservaDetails(reservaId))
                },
                aeronaveSeleccionadaId = args.reservaId

            )
        }



        composable<Screen.ReservaDelete> {
            val args = it.toRoute<Screen.ReservaDelete>()
            ReservaDeleteScreen(
                reservaId = args.reservaId,
                goBack = {
                    navController.navigate(Screen.Reserva)
                },
            )

        }

        composable<Screen.PagoReserva> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Rutas_y_viajes)
            }

            val reservaViewModel: ReservaViewModel = hiltViewModel(parentEntry)
            val args = backStackEntry.toRoute<Screen.PagoReserva>()
            PagoReservaListScreen(
                pagoReservaId = args.pagoReservaId,
                goBack = {
                    navController.navigate(Screen.Reserva)
                },
                rutaList = rutaList,
                tipoVueloList = tipoList,
                viewModel = reservaViewModel

            )
        }


        composable<Screen.RutaDetailsScreen> { backStack ->
            val args = backStack.toRoute<Screen.RutaDetailsScreen>()
            val viewModel: RutaViewModel = hiltViewModel()
            RutaScreenDetails(
                rutaId = args.rutaId,
                viewModel = viewModel,
                goBack = { navController.popBackStack() },
                onDelete = { id ->
                    viewModel.onEvent(RutaEvent.Delete)
                },
                onEdit = { id ->
                    navController.navigate(Screen.Ruta(id))
                }
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
                goToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.TipoVuelo> { backStack ->
            val args = backStack.toRoute<Screen.TipoVuelo>()
            TipoVueloScreen(
                tipoVueloId = args.tipovueloId,
                goBack = { navController.popBackStack() }
            )
        }

        composable<Screen.TipoVueloDetails> { backStack ->
            val args = backStack.toRoute<Screen.TipoVueloDetails>()
            val viewModel: TipoVueloViewModel = hiltViewModel()
            TipoVueloDetailsScreen(
                tipoVueloId = args.tipovueloId,
                viewModel = viewModel,
                goBack = { navController.popBackStack() },
                onDelete = { id ->
                    viewModel.onEvent(TipoVueloEvent.Delete)
                },
                onEdit = { id ->
                    navController.navigate(Screen.TipoVuelo(id))
                }
            )
        }

        composable<Screen.CategoriaAeronaveList> {
            CategoriaAeronaveListScreen(
                goToCategoria = { categoriaId ->
                    navController.navigate("aeronaveList?categoriaId=$categoriaId")
                },
                createCategoria = {
                    navController.navigate(Screen.CategoriaAeronave(null))
                },
                goToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                goBack = { navController.popBackStack() },
                onEdit = { categoriaId ->
                    navController.navigate(Screen.CategoriaAeronave(categoriaId))
                }
            )
        }

        //Pantalla de aeronave reserva
        composable<Screen.CategoriaAeronaveReservaList> {
            CategoriaReservaAeronaveScreen (
                goToCategoria = { categoriaId ->
                    navController.navigate("tipoAeronaveList?categoriaId=$categoriaId")
                },
                goBack = { navController.popBackStack() },
                navController = navController
            )
        }

        //Lista de aeronaves por la categoria seleccionada
        composable(
            route = "tipoAeronaveList?categoriaId={categoriaId}",
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
            TipoAeronaveListScreen(
                goToAeronave = {aeronaveId->
                    navController.navigate(Screen.TipoAeronaveDetails(aeronaveId))
                },
                goBack = { navController.popBackStack() }
            )
        }


        //Detalles de la aeronave
        composable<Screen.TipoAeronaveDetails> { backStack ->
            val args = backStack.toRoute<Screen.TipoAeronaveDetails>()
            val viewModel: AeronaveViewModel = hiltViewModel()
            TipoAeronaveDetailsScreen(
                aeronaveId = args.aeronaveId,
                ViewModel = viewModel,
                goBack = { navController.popBackStack() },
                onReservar = {
                    navController.navigate(Screen.Rutas_y_viajes)
                }
            )
        }


            composable<Screen.CategoriaAeronave> { backStack ->
                val args = backStack.toRoute<Screen.CategoriaAeronave>()
                CategoriaAeronaveScreen(
                    categoriaId = args.categoriaId,
                    goBack = { navController.popBackStack() }
                )
            }

        composable<Screen.AdminPanel> { backStackEntry ->
            val adminPanel = backStackEntry.toRoute<Screen.AdminPanel>()
            AdminPanelScreen(
                adminId = adminPanel.adminId,
                navController = navController,
                goBack = { navController.popBackStack() },
                goToPerfil = { adminId ->
                    navController.navigate(Screen.PerfilAdmin(adminId))
                },
                aeronaveViewModel = hiltViewModel(),
                reservaViewModel = hiltViewModel()
            )
        }

        composable<Screen.PerfilAdmin> { backStackEntry ->
            val perfilAdmin = backStackEntry.toRoute<Screen.PerfilAdmin>()
            PerfilAdminScreen(
                adminId = perfilAdmin.adminId,
                navController = navController,
                goToAdminPanel = {
                    navController.navigate(
                        Screen.AdminPanel(
                            adminId = perfilAdmin.adminId
                        )
                    )
                },
                goToFirstScreen = {
                    navController.navigate(Screen.FirstScreen)
                },
                goBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "aeronaveList?categoriaId={categoriaId}&adminId={adminId}",
            arguments = listOf(
                navArgument("categoriaId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("adminId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getInt("categoriaId") ?: -1
            val adminId = backStackEntry.arguments?.getInt("adminId") ?: 1
            val viewModel: AeronaveViewModel = hiltViewModel()
            LaunchedEffect(categoriaId) {
                if (categoriaId != -1) viewModel.filterAeronavesByCategoria(categoriaId)
            }
            AeronaveListScreen(
                goToAeronave = { id ->
                    navController.navigate(Screen.AeronaveDetailsScreen(id))
                },
                createAeronave = {
                    navController.navigate(Screen.Aeronave(null))
                },
                goToAdminPanel = {
                    navController.navigate(Screen.AdminPanel)
                },
                goBack = { navController.popBackStack() },
            )
        }

            composable<Screen.Aeronave> { backStack ->
                val args = backStack.toRoute<Screen.Aeronave>()
                AeronaveScreen(
                    aeronaveId = args.aeronaveId,
                    goBack = { navController.popBackStack() }
                )
            }

            composable<Screen.AeronaveDetailsScreen> { backStack ->
                val args = backStack.toRoute<Screen.AeronaveDetailsScreen>()
                val viewModel: AeronaveViewModel = hiltViewModel()
                AeronaveDetailsScreen(
                    aeronaveId = args.aeronaveId,
                    viewModel = viewModel,
                    goBack = { navController.popBackStack() },
                    onDelete = { id ->
                        viewModel.onEvent(AeronaveEvent.Delete)
                    },
                    onEdit = { id ->
                        navController.navigate(Screen.Aeronave(id))
                    }
                )

                 }
            }
        }


