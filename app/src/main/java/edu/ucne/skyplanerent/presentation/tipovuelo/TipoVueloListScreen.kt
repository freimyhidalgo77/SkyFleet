package edu.ucne.skyplanerent.presentation.tipovuelo

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
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.ruta.RutaUiState

@Composable
fun TipoVueloListScreen(
    viewModel: TipoVueloViewModel = hiltViewModel(),
    goToTipoVuelo: (Int) -> Unit,
    createTipoVuelo: () -> Unit,
    deleteTipoVuelo: ((TipoVueloEntity) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TipoVueloListBodyScreen(
        uiState = uiState,
        goToTipoVuelo = goToTipoVuelo,
        createTipoVuelo = createTipoVuelo,
        deleteTipoVuelo = { tipovuelo ->
            viewModel.onEvent(TipoVueloEvent.TipoVueloChange(tipovuelo.vueloId ?: 0))
            viewModel.onEvent(TipoVueloEvent.Delete)
        }
    )
}


@Composable
private fun TipoVueloRow(
    it: TipoVueloEntity,
    goToTipoVuelo: () -> Unit,
    deleteTipoVuelo:(TipoVueloEntity) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = it.rutaId.toString(),
            color = Color.Black
        )
        Text(
            modifier = Modifier.weight(2f),
            text = it.tipoClienteId.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(
            modifier = Modifier.weight(2f),
            text = it.descripcionTipoVuelo,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(
            modifier = Modifier.weight(2f),
            text = it.precio.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Text(modifier = Modifier.weight(2f), text = it.rutaId.toString(), color = Color.Black)
        IconButton(onClick = goToTipoVuelo) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = {deleteTipoVuelo(it)}) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
        }

    }
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoVueloListBodyScreen(
    uiState: TipoVueloUiState,
    goToTipoVuelo: (Int) -> Unit,
    createTipoVuelo: () -> Unit,
    deleteTipoVuelo: (TipoVueloEntity) -> Unit
){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tipo Vuelo") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = createTipoVuelo
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create a new TipoVuelo"
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
                items(uiState.tipovuelo) { tipovuelo ->
                    TipoVueloRow(
                        it = tipovuelo,
                        goToTipoVuelo = { goToTipoVuelo(tipovuelo.vueloId ?: 0) },
                        deleteTipoVuelo = deleteTipoVuelo
                    )
                }
            }
        }
    }
}