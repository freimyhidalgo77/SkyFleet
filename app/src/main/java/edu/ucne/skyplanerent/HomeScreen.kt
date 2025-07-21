package edu.ucne.skyplanerent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.ucne.skyplanerent.presentation.navigation.BottomNavItem
import edu.ucne.skyplanerent.presentation.navigation.Screen

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToReserva: () -> Unit,
    onNavigateToRutas_Viajes: () -> Unit,
    navController: NavController
) {
    val items = listOf(
        BottomNavItem("Reservas", Icons.Default.List, Screen.Reserva),
        BottomNavItem("Aronaves", Icons.Default.List, Screen.CategoriaAeronaveReservaList),
        BottomNavItem("Rutas y Viajes", Icons.Default.List, Screen.Rutas_y_viajes),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil),
        //BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoskyfleet),
                contentDescription = "Logo de SkyFleet",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(9.dp))

            Image(
                painter = painterResource(id = R.drawable.c172welcome),
                contentDescription = "Logo de bienvenida!",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                "Bienvenido!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 7.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLogout)
            {
                Text("Cerrar sesión")
            }
        }
    }
}





