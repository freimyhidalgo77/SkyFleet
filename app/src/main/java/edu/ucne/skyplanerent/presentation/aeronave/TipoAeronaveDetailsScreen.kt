package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import edu.ucne.skyplanerent.presentation.navigation.BottomNavItem
import edu.ucne.skyplanerent.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoAeronaveDetailsScreen (
    aeronaveId: Int?,
    ViewModel: AeronaveViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onReservar: () -> Unit,
    navController: NavController
) {
    val uiState by ViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(aeronaveId) {
        aeronaveId?.let {
            if (it > 0) {
                ViewModel.onEvent(AeronaveEvent.GetAeronave(it))
            }
        }
    }

    LaunchedEffect(Unit) {
        ViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> goBack()
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    TipoAeronaveDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        snackbarHostState = snackbarHostState,
        onReservar = onReservar,
        navController = navController

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoAeronaveDetailsBodyScreen (
    uiState: AeronaveUiState,
    goBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onReservar: () -> Unit,
    navController:NavController
) {
    val refreshing = uiState.isLoading


    val items = listOf(
        BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de la Aeronave",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route.toString(),
                        onClick = {
                            if (currentRoute != item.route.toString()) {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home) { inclusive = false }
                                }
                            }
                        }
                    )
                }
            }
        },

        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.isSuccess) Color.Green.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        // Imagen principal
                        when {
                            uiState.imageUrl != null -> {
                                AsyncImage(
                                    model = uiState.imageUrl,
                                    contentDescription = "Imagen de ${uiState.ModeloAvion}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                                )
                            }
                            uiState.imageUri != null -> {
                                AsyncImage(
                                    model = uiState.imageUri,
                                    contentDescription = "Imagen de ${uiState.ModeloAvion}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                                )
                            }
                            else -> {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                    }


                    item {
                        if (uiState.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                    } else if (uiState.AeronaveId != null) {

                        Text(
                            text = "Especificaciones Clave",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Capacidad", color = Color.Gray)
                                Text("${uiState.CapacidadPasajeros ?: "0"} Personas", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Tipo", color = Color.Gray)
                                Text("${uiState.DescripcionCategoria ?: "N/A"}", color = Color.Black)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Peso Máximo", color = Color.Gray)
                                Text("${uiState.Peso ?: "0.0"} kg", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Rango", color = Color.Gray)
                                Text("${uiState.Rango ?: "0"} NM", color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección: Especificaciones
                        Text(
                            text = "Especificaciones",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Motor", color = Color.Gray)
                                Text("${uiState.DescripcionMotor ?: "N/A"}", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Combustible", color = Color.Gray)
                                Text("${uiState.CapacidadCombustible ?: "0"} gal", color = Color.Black)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Velocidad de Crucero", color = Color.Gray)
                                Text("${uiState.VelocidadMaxima ?: "0"} KTAS", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Rango", color = Color.Gray)
                                Text("${uiState.Rango ?: "0"} NM", color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección: Información Operacional
                        Text(
                            text = "Información Operacional",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column {
                            Text("Modelo", color = Color.Gray)
                            Text("${uiState.ModeloAvion ?: "N/A"}", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Registración", color = Color.Gray)
                            Text("${uiState.Registracion ?: "N/A"}", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Licencia", color = Color.Gray)
                            Text("${uiState.Licencia ?: "N/A"}", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Costo por Hora", color = Color.Gray)
                            Text("$${uiState.CostoXHora ?: "0.0"}", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Consumo por Hora", color = Color.Gray)
                            Text("${uiState.ConsumoXHora ?: "0"} L/h", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Descripción", color = Color.Gray)
                            Text("${uiState.DescripcionAeronave ?: "N/A"}", color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = onReservar,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0A80ED),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Reservar ahora")
                            }
                        }

                       } else {
                        Text(
                            text = "No se encontraron detalles de la aeronave",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    uiState.errorMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Indicador de carga si está activo
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}