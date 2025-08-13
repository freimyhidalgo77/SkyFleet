package edu.ucne.skyplanerent.presentation.pago


data class DatosTransferencia(
    val banco: String,
    val numeroCuenta: String,
    val nombreTitular: String,
    val referencia: String,
    val monto: Double
)
