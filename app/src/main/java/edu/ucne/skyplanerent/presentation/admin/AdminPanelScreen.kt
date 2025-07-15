package edu.ucne.skyplanerent.presentation.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edu.ucne.skyplanerent.presentation.navigation.Screen

@Composable
fun AdminPanelScreen(
    navController: NavController,
    goBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Panel de Administrador",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Total de vuelos", fontSize = 16.sp)
                        Text(text = "1,234", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Aeronaves activas", fontSize = 16.sp)
                        Text(text = "19", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Usuarios registrados", fontSize = 16.sp)
                        Text(text = "567", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Total de reservas", fontSize = 16.sp)
                        Text(text = "890", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }

        item {
            Text(
                text = "Tendencias",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Estadísticas de vuelos", fontSize = 16.sp)
                    Text(text = "+12%", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Últimos 30 días +12%", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp))
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Estadísticas de reservas", fontSize = 16.sp)
                    Text(text = "-5%", fontSize = 24.sp, color = MaterialTheme.colorScheme.error)
                    Text(text = "Últimos 30 días -5%", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp))
                }
            }
        }

        item {
            Text(
                text = "Seleccionar una opción",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Aeronaves")
                        Text(text = "Gestionar aeronaves", fontSize = 16.sp)
                    }
                }
                Card(
                    onClick = { navController.navigate(Screen.RutaList) },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Rutas")
                        Text(text = "Gestionar rutas", fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Card(
                onClick = { navController.navigate(Screen.TipoVueloList) },
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Tipos de Vuelo")
                    Text(text = "Gestionar tipos de vuelo", fontSize = 16.sp)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 32.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Vuelos")
                    Text(text = "Gestionar vuelos", fontSize = 16.sp)
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                IconButton(onClick = { /* Navegar a Home */ }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                }
                IconButton(onClick = { /* Navegar a Perfil */ }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil")
                }
            }
        }
    }
}