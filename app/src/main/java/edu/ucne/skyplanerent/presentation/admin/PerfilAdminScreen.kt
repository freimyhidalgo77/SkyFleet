package edu.ucne.skyplanerent.presentation.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.ucne.skyplanerent.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdminScreen(
    navController: NavController,
    goBack: () -> Unit,
    goToAdminPanel: () -> Unit,
    goToFirstScreen: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Perfil",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.admin),
                            contentDescription = "Admin Panel",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Admin") },
                    selected = false,
                    onClick = goToAdminPanel
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Blue // Resaltado como seleccionado
                        )
                    },
                    label = { Text("Perfil") },
                    selected = true,
                    onClick = {} // No navega a ningún lado, ya estamos en esta página
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SkyrentPlaneAdministration",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Admin",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Email
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Email: Admin@gmail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Divider(color = Color.Gray.copy(alpha = 0.2f))

            // Sección de Permisos
            Text(
                text = "Permisos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Permiso 1: Gestión de aeronaves
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.Flight,
                    contentDescription = "Gestión de aeronaves",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gestión de aeronaves",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Permiso 2: Gestión de rutas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Gestión de rutas",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gestión de rutas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Permiso 3: Gestión de tipos de vuelo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Gestión de tipos de vuelo",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gestión de tipos de vuelo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Salir
            Button(
                onClick = goToFirstScreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Salir",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salir",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}