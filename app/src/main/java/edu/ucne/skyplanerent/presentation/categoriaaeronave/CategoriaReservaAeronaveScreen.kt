package edu.ucne.skyplanerent.presentation.categoriaaeronave

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import coil.compose.AsyncImage

@Composable
fun CategoriaReservaAeronaveScreen (
    viewModel: CategoriaAeronaveViewModel = hiltViewModel(),
    goToCategoria: (Int) -> Unit,

    deleteCategoria: ((CategoriaAeronaveEntity) -> Unit)? = null,
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CategoriaReservaAeronaveScreen(
        uiState = uiState,
        goToCategoria = goToCategoria,
        deleteCategoria = { categoria ->
            viewModel.onEvent(CategoriaAeronaveEvent.CategoriaIdChange(categoria.categoriaId ?: 0))
            viewModel.onEvent(CategoriaAeronaveEvent.Delete)
        },
        goBack = goBack,
    )
}

@Composable
private fun CategoriaAeronaveRow(
    it: CategoriaAeronaveEntity,
    goToCategoria: (Int) -> Unit,
    deleteCategoria: (CategoriaAeronaveEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { goToCategoria(it.categoriaId ?: 0) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {

        it.imagePath?.let { path ->
            AsyncImage(
                model = path,
                contentDescription = "Imagen de ${it.descripcionCategoria}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

        } ?: Spacer(modifier = Modifier.size(100.dp))

        Spacer(modifier = Modifier.height(8.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = it.categoriaId.toString(),
                color = Color.Black
            )
            Text(
                modifier = Modifier.weight(2f),
                text = it.descripcionCategoria,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaReservaAeronaveScreen(
    uiState: CategoriaAeronaveUiState,
    goToCategoria: (Int) -> Unit,
    deleteCategoria: (CategoriaAeronaveEntity) -> Unit,
    goBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Explora nuestras aeronaves") },

                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explore nuestra flota de aeronaves, desde monomotores a " +
                        "piston hasta lujosos jets ejecutivos. Encuentre el avión" +
                        " perfecto para su próxima aventura!",
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.categorias) { categoria ->
                    CategoriaAeronaveRow(
                        it = categoria,
                        goToCategoria = { categoriaId -> goToCategoria(categoriaId) }, // Pasar categoriaId
                        deleteCategoria = deleteCategoria
                    )
                }
            }
        }
    }
}
