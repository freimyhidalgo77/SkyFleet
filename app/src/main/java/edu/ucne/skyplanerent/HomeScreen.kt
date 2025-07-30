package edu.ucne.skyplanerent

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import edu.ucne.skyplanerent.data.repository.UserRepository
import edu.ucne.skyplanerent.presentation.navigation.BottomNavItem
import edu.ucne.skyplanerent.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToReserva: () -> Unit,
    onNavigateToRutas_Viajes: () -> Unit,
    onNavigateToPeril:()->Unit,
    navController: NavController,
    userRepository: UserRepository,
    currentUserEmail: String?
) {
    val items = listOf(
        BottomNavItem("Reservas", Icons.Default.Book, Screen.Reserva),
        BottomNavItem("Aronaves", Icons.Default.AirplanemodeActive, Screen.CategoriaAeronaveReservaList),
        BottomNavItem("Rutas y Viajes", Icons.Default.Map, Screen.Rutas_y_viajes),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil),
        //BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
    )

    var user = remember { mutableStateOf<UserRegisterAccount?>(null) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(currentUserEmail) {
        if (currentUserEmail != null) {
            user.value = userRepository.getUserByEmail(currentUserEmail)

        }
    }

    Scaffold(
        topBar = {},
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route.toString(),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.logoskyfleet),
                    contentDescription = "Logo SkyFleet",
                    modifier = Modifier
                        .size(150.dp)
                        //.padding(top = 16.dp)
                )
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                        //.padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.c172welcome),
                        contentDescription = "Promoción",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.height(200.dp)
                    )
                }
            }

            item {
                Text(
                    "¡Bienvenido ${user.value?.nombre?:"Freimy"}!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                Text(
                    "¿Qué deseas hacer hoy?",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp) // Fixed height to prevent infinite constraints
                        .padding(horizontal = 16.dp),
                    content = {
                        item {
                            ActionCard(
                                icon = Icons.Default.AirplanemodeActive,
                                title = "Reservar Vuelo",
                                onClick = { navController.navigate(Screen.Reserva) }
                            )
                        }
                        item {
                            ActionCard(
                                icon = Icons.Default.Map,
                                title = "Explorar Rutas",
                                onClick = { navController.navigate(Screen.Rutas_y_viajes) }
                            )
                        }
                        item {
                            ActionCard(
                                icon = Icons.Default.List,
                                title = "Mis Reservas",
                                onClick = { navController.navigate("mis_reservas") }
                            )
                        }
                        item {
                            ActionCard(
                                icon = Icons.Default.Star,
                                title = "Ofertas",
                                onClick = { navController.navigate("ofertas") }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ActionCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title)
        }
    }
}


