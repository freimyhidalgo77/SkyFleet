package edu.ucne.skyplanerent.presentation.pago

data class DatosTarjetaCredito(
    val numeroTarjeta: String,
    val nombreTitular: String,
    val fechaExpiracion: String,
    val cvv: String,
    val tipoTarjeta: String // Visa, MasterCard, etc.
)