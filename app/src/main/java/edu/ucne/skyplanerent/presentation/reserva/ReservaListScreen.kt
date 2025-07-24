package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.presentation.navigation.BottomNavItem
import edu.ucne.skyplanerent.presentation.navigation.Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReservaListScreen(
    viewModel: ReservaViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    scope: CoroutineScope,
    onCreate:()-> Unit,
    onDetails: (Int) -> Unit,
    onEdit:(ReservaEntity)-> Unit,
    onDelete:(ReservaEntity)-> Unit,
    navController: NavController

){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()

    ReservaBodyListScreen(
        uiState = uiState,
        scope = scope,
        onCreate = onCreate,
        onEdit = onEdit,
        onDelete = onDelete,
        onDetails = onDetails,
        rutaUiState = rutaUiState,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaBodyListScreen(
    uiState: UiState,
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onDetails: (Int) -> Unit,
    onEdit: (ReservaEntity) -> Unit,
    onDelete: (ReservaEntity) -> Unit,
    rutaUiState: RutaUiState,
    navController: NavController // <-- Agregado
) {
    val items = listOf(
        BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis Reservas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.reservas),
                contentDescription = "Imagen mis reservas",
                modifier = Modifier
                    .height(190.dp)
                    .fillMaxSize()
            )

            if (uiState.reservas.isEmpty()) {
                Text(
                    text = "No se han encontrado reservas.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.reservas) { reserva ->
                        ReservaRow(reserva, onDetails, rutaUiState)
                    }
                }
            }
        }
    }
}


@Composable
fun ReservaRow(
    reserva: ReservaEntity,
    onDetails: (Int) -> Unit,
    rutaUiState: RutaUiState,
    reservaViewModel: ReservaViewModel = hiltViewModel()
) {
    val formato = SimpleDateFormat("MMMM d, yyyy • h:mm a", Locale("es", "ES"))
    val fechaFormateada = reserva.fecha?.let { formato.format(it) } ?: "Sin fecha"

    val ruta = rutaUiState.rutas.find { it.rutaId == reserva.rutaId }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {

            Text(
                text = "Reserva #${reserva.reservaId}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = fechaFormateada,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            Text(
                text = ruta?.let { "Desde: ${it.origen} hacia ${it.destino}" } ?: "Ruta no disponible",
                fontSize = 15.sp,
                color = Color.DarkGray
            )

            //Ver mas
            androidx.compose.material3.Button(

                onClick = { onDetails(reserva.reservaId?: 0) },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.Start),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A80ED),
                    contentColor = Color.White
                )
            ) {
                Text("Ver más")
            }
        }
    }
}
