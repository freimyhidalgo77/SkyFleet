package edu.ucne.skyplanerent.presentation.ruta

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.local.entity.RutaEntity

@Composable
fun RutaListScreen(
    viewModel: RutaViewModel = hiltViewModel(),
    goToRuta: (Int) -> Unit,
    createRuta: () -> Unit,
    deleteRuta: ((RutaEntity) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RutaListBodyScreen(
        uiState = uiState,
        goToRuta = goToRuta,
        createRuta = createRuta,
        deleteRuta = { rutas ->
            viewModel.onEvent(RutaEvent.RutaChange(rutas.rutaId ?: 0))
            viewModel.onEvent(RutaEvent.Delete)
        }
    )
}


@Composable
private fun RutaRow(
    it: RutaEntity,
    goToRuta: () -> Unit,
    deleteRuta:(RutaEntity) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(modifier = Modifier.weight(1f), text = it.rutaId.toString(), color = Color.Black)
        Text(
            modifier = Modifier.weight(2f),
            text = it.origen,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(
            modifier = Modifier.weight(2f),
            text = it.destino,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(
            modifier = Modifier.weight(2f),
            text = it.distancia,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(
            modifier = Modifier.weight(2f),
            text = it.duracionEstimada,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(modifier = Modifier.weight(2f), text = it.aeronaveId.toString(), color = Color.Black)
        IconButton(onClick = goToRuta) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = {deleteRuta(it)}) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
        }

    }
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaListBodyScreen(
    uiState: RutaUiState,
    goToRuta: (Int) -> Unit,
    createRuta: () -> Unit,
    deleteRuta: (RutaEntity) -> Unit
){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Lista de Rutas") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = createRuta
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create a new Ruta"
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.rutas) { ruta ->
                    RutaRow(
                        it = ruta,
                        goToRuta = { goToRuta(ruta.rutaId ?: 0) },
                        deleteRuta = deleteRuta
                    )
                }
            }
        }
    }
}