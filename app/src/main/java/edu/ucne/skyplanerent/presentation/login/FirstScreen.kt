package edu.ucne.skyplanerent.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.presentation.navigation.Screen

@Composable
fun FirstScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

   /* LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            // Si hay un usuario autenticado, navega directo a Home
            navController.navigate(Screen.Home) {
                popUpTo(Screen.FirstScreen) { inclusive = true }
            }
        }
    }*/

    // UI de bienvenida
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoskyfleet),
            contentDescription = "Logo de Skyfleet",
            modifier = Modifier
                .height(200.dp)
                .fillMaxSize()
        )
        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Reserva tu avión con solo unos clics!",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.Login(0))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A80ED),
                    contentColor = Color.White
                )
            ) {
                Text("Iniciar sesión", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Register)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A80ED),
                    contentColor = Color.White
                )
            ) {
                Text("Registrarse", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.AdminPanel)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A80ED),
                    contentColor = Color.White
                )
            ) {
                Text("Ir a Panel de Administrador", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}




