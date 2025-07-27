package edu.ucne.skyplanerent.presentation.reserva

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.navigation.Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.times

@Composable
fun PagoReservaListScreen(
    pagoReservaId:Int,
    /*TipoVueloviewModel: TipoVueloViewModel = hiltViewModel(),
    RutaviewModel: RutaViewModel = hiltViewModel(),
    tipoVueloId:Int,
    RutaId:Int,*/
    tipoVueloList: List<TipoVueloEntity>,
    rutaList: List<RutaEntity>,
    viewModel: ReservaViewModel,
    rutaViewModel: RutaViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    formularioViewModel: FormularioViewModel = hiltViewModel(),
    goBack:()->Unit

){

    LaunchedEffect(pagoReservaId) {
        if (pagoReservaId > 0) {
            formularioViewModel.selectedFormulario(pagoReservaId)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaevUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()
    val formularioUiState by formularioViewModel.uiState.collectAsStateWithLifecycle()


    PagoReservaBodyListScreen(
        uiState = uiState,
        //scope = scope,
        tipoVueloList = tipoVueloList,
        rutaList = rutaList,
        /* onCreate = onCreate,
         onEdit = onEdit,
         onDelete = onDelete,*/
        PagoReservaId = pagoReservaId,
        goBack = goBack,
        rutaUiState = rutaUiState,
        tipoVueloUiState = tipoVueloUiState,
        reservaViewModel = viewModel,
        aeronaveUiState = aeronaevUiState,
        formularioUiState = formularioUiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoReservaBodyListScreen(
    uiState: UiState,
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel,
    // scope: CoroutineScope,
    tipoVueloList:List<TipoVueloEntity>,
    rutaList:List<RutaEntity>,
    PagoReservaId:Int,
    rutaUiState: RutaUiState,
    aeronaveUiState:AeronaveUiState,
    tipoVueloUiState: TipoVueloUiState,
    formularioUiState: FormularioUiState,
    goBack:()->Unit

) {
    val navController = rememberNavController()

    val idTipoVueloSeleccionado by reservaViewModel.tipoVueloSeleccionadoId.collectAsState()
    val tipoVueloSeleccionado = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == idTipoVueloSeleccionado }

    val idRutaSeleccionada by reservaViewModel.rutaSeleccionadaId.collectAsState()
    val rutaSeleccionada = rutaUiState.rutas.find { it.rutaId == idRutaSeleccionada }

    val idAeronaveSeleccionada by reservaViewModel.tipoAeronaveSeleccionadaId.collectAsState()
    val aeronaveSeleccionada = aeronaveUiState.aeronaves.find { it.aeronaveId == idAeronaveSeleccionada }

    val fechaVuelo by reservaViewModel.fechaSeleccionada.collectAsState()

    // Valores base
    val duracionVuelo = rutaSeleccionada?.duracion?:0
    val costoXHora = aeronaveSeleccionada?.costoXHora ?: 0.0
    val tarifaBase = duracionVuelo * costoXHora
    val impuesto = tarifaBase * 0.10
    val precioTotal = tarifaBase + impuesto



    val tipoCliente by reservaViewModel.tipoCliente.collectAsState()

    val reservaUiState by reservaViewModel.uiState.collectAsStateWithLifecycle()
    val licenciaSeleccionada = reservaUiState.licenciaPiloto


    var metodoPagoSeleccionado by remember { mutableStateOf<MetodoPago?>(null) }

    /*Calcular
     val tarifaBase = duracionVuelo * costoXHora
     val impuesto = tarifaBase * 0.10
     val precioTotal = tarifaBase + impuesto*/


    var datosTransferencia by remember { mutableStateOf<DatosTransferencia?>(null) }
    var mostrarFormularioTransferencia by remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Pago Reserva",
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {

            item {
                Text(
                    text = "Tipo de vuelo: ${tipoVueloSeleccionado?.nombreVuelo ?: "No seleccionado"}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Text(
                    text = rutaSeleccionada?.let { "Origen: ${it.origen}" } ?: "No seleccionado",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Text(
                    text = rutaSeleccionada?.let { "Destino: ${it.destino}" } ?: "No seleccionado",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Text(
                    text = "Aeronave seleccionada: ${aeronaveSeleccionada?.modeloAvion ?: "No seleccionado"}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            fechaVuelo?.let { fecha ->
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaFormateada = formato.format(fecha)

                item {
                    Text(
                        text = "Fecha seleccionada: $fechaFormateada",
                        fontSize = 16.sp,
                        color = Color(0xFF0A80ED),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    Text(
                        text = rutaSeleccionada?.let { "Duración estimada: ${it.duracion}" }
                            ?: "Duración: No disponible",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Nombre ${formularioUiState.nombre} Apellido ${formularioUiState.apellido}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Pasaporte ${formularioUiState.pasaporte}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }


                item {
                    Text(
                        text = "Cantidad pasajeros ${formularioUiState.cantidadPasajeros}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(text = "¿Es Piloto?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = when (tipoCliente) {
                            true -> "Sí"
                            false -> "No"
                            else -> "No especificado"
                        },
                        fontSize = 16.sp,
                        color = Color.Gray

                    )
                }

                item {
                    Text(
                        text = "Licencia seleccionada: ${licenciaSeleccionada ?: "No aplica"}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }


                item {
                    Text(
                        text = "Desgloce de precio: $tarifaBase",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Impuesto: $impuesto",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Precio total: $precioTotal",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }


                item {
                    Text(
                        text = "Método de pago",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Tarjeta de crédito
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                metodoPagoSeleccionado = MetodoPago.TARJETA_CREDITO
                                mostrarFormularioTransferencia = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (metodoPagoSeleccionado == MetodoPago.TARJETA_CREDITO)
                                Color.LightGray else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Tarjeta de crédito"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Tarjeta de crédito")
                        }
                    }
                    /*  // PayPal
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { metodoPagoSeleccionado = MetodoPago.PAYPAL },
                        colors = CardDefaults.cardColors(
                            containerColor = if (metodoPagoSeleccionado == MetodoPago.PAYPAL)
                                Color.LightGray else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.paypal), // Asegúrate de tener este icono
                                contentDescription = "PayPal"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("PayPal")
                        }
                    }*/

                    // Transferencia bancaria
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                metodoPagoSeleccionado = MetodoPago.TRANSFERENCIA_BANCARIA
                                mostrarFormularioTransferencia = true
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (metodoPagoSeleccionado == MetodoPago.TRANSFERENCIA_BANCARIA)
                                Color.LightGray else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Transferencia bancaria"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Transferencia bancaria")
                        }
                    }
                }

                item {
                    // En el item donde llamas al formulario
                    // Dentro de tu composable principal (PagoReservaBodyListScreen o similar)
                    if (mostrarFormularioTransferencia) {
                        FormularioTransferenciaBancaria(
                            precioTotal = precioTotal,
                            viewModel = reservaViewModel,  // Pasa el ViewModel
                            onConfirmarTransferencia = { datos ->
                                // Puedes hacer algo adicional aquí si lo necesitas
                                mostrarFormularioTransferencia = false

                            },
                            onCancelar = {
                                mostrarFormularioTransferencia = false
                            },
                            rutaId = rutaSeleccionada?.rutaId ?: 0,
                            tipoVueloId = tipoVueloSeleccionado?.tipoVueloId ?: 0,
                            aeronaveId = aeronaveSeleccionada?.aeronaveId ?: 0,
                            tipoCliente = tipoCliente ?: false,
                            pasajeros = formularioUiState.cantidadPasajeros,
                            formularioId = formularioUiState.formularioId?:0,
                            goBack = goBack
                        )
                    }

                }

                /*  item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val rutaId = rutaSeleccionada?.rutaId ?: return@Button
                            val tipoVueloId = tipoVueloSeleccionado?.tipoVueloId ?: return@Button
                            val aeronaveId = aeronaveSeleccionada?.aeronaveId ?: return@Button
                            val tipoCliente = uiState?.tipoCliente ?: return@Button
                            val pasajero = formularioUiState.cantidadPasajeros

                            // Guardar los datos de transferencia si el método es transferencia bancaria
                            val comprobante = if (metodoPagoSeleccionado == MetodoPago.TRANSFERENCIA_BANCARIA) {
                                datosTransferencia?.let {
                                    "Banco: ${it.banco}, Cuenta: ${it.numeroCuenta}, " +
                                            "Titular: ${it.nombreTitular}, Referencia: ${it.referencia}"
                                } ?: ""
                            } else {
                                ""
                            }

                            reservaViewModel.guardarReserva(
                                rutaId = rutaId,
                                tipoVueloId = tipoVueloId,
                                aeronaveId = aeronaveId,
                                tarifaBase = tarifaBase,
                                impuesto = impuesto,
                                precioTotal = precioTotal,
                                tipoCliente = tipoCliente,
                                pasajero = pasajero,
                                metodoPago = metodoPagoSeleccionado?.name,
                                comprobante = comprobante
                            )

                            goBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0A80ED),
                            contentColor = Color.White
                        ),
                        enabled = when (metodoPagoSeleccionado) {
                            MetodoPago.TRANSFERENCIA_BANCARIA -> datosTransferencia != null
                            else -> true
                        }
                    ) {
                        Text("Realizar pago")
                    }
                }
            }*/
                /* Button(
                        onClick = { goBack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFED0A0A),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancelar Pago")
                    }*/

                    }

                }
            }
        }

@Composable
fun FormularioTransferenciaBancaria(
    precioTotal: Double,
    viewModel: ReservaViewModel,  // Añade el ViewModel como parámetro
    onConfirmarTransferencia: (DatosTransferencia) -> Unit,
    onCancelar: () -> Unit,
    rutaId: Int,                 // Parámetros necesarios para guardar la reserva
    tipoVueloId: Int,
    aeronaveId: Int,
    formularioId:Int,
    tipoCliente: Boolean,
    pasajeros: Int,
    goBack:()->Unit

) {
    var bancoSeleccionado by remember { mutableStateOf("") }
    var numeroCuenta by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }

    val bancos = listOf(
        "Banco Popular",
        "Banco BHD León",
        "Banco Reservas",
        "Banco Santa Cruz",
        "Otro banco"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Transferencia Bancaria",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Total a pagar: RD$${"%.2f".format(precioTotal)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Banco
            Text("Banco:", modifier = Modifier.padding(top = 8.dp))
            bancos.forEach { banco ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { bancoSeleccionado = banco }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = bancoSeleccionado == banco,
                        onClick = { bancoSeleccionado = banco }
                    )
                    Text(
                        text = banco,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Número de cuenta
            OutlinedTextField(
                value = numeroCuenta,
                onValueChange = { numeroCuenta = it },
                label = { Text("Número de cuenta destino") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Nombre del titular
            OutlinedTextField(
                value = nombreTitular,
                onValueChange = { nombreTitular = it },
                label = { Text("Nombre del titular") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Referencia
            OutlinedTextField(
                value = referencia,
                onValueChange = { referencia = it },
                label = { Text("Número de referencia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Instrucciones
            Text(
                text = "Por favor realice la transferencia a la cuenta indicada y proporcione el número de referencia.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancelar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (bancoSeleccionado.isNotEmpty() &&
                            numeroCuenta.isNotEmpty() &&
                            nombreTitular.isNotEmpty() &&
                            referencia.isNotEmpty()) {

                            val datosTransferencia = DatosTransferencia(
                                banco = bancoSeleccionado,
                                numeroCuenta = numeroCuenta,
                                nombreTitular = nombreTitular,
                                referencia = referencia,
                                monto = precioTotal
                            )

                            // Crear el comprobante como string
                            val comprobante = "Banco: ${datosTransferencia.banco}, " +
                                    "Cuenta: ${datosTransferencia.numeroCuenta}, " +
                                    "Titular: ${datosTransferencia.nombreTitular}, " +
                                    "Referencia: ${datosTransferencia.referencia}"

                            // Llamar al ViewModel para guardar la reserva
                            viewModel.guardarReserva(
                                rutaId =  rutaId,
                                tipoVueloId = tipoVueloId,
                                aeronaveId = aeronaveId,
                                tarifaBase = precioTotal / 1.1, // Asumiendo 10% de impuesto
                                impuesto = precioTotal * 0.1,
                                precioTotal = precioTotal,
                                tipoCliente = tipoCliente,
                                pasajero = pasajeros,
                                metodoPago = "TRANSFERENCIA_BANCARIA",
                                comprobante = comprobante,
                                formularioId = formularioId

                            )

                            // Notificar que la transferencia fue confirmada
                            onConfirmarTransferencia(datosTransferencia)
                            goBack()
                        }
                    },
                    enabled = bancoSeleccionado.isNotEmpty() &&
                            numeroCuenta.isNotEmpty() &&
                            nombreTitular.isNotEmpty() &&
                            referencia.isNotEmpty()
                ) {
                    Text("Confirmar Transferencia")
                }
            }
        }
    }
}


