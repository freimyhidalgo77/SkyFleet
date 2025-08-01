package edu.ucne.skyplanerent.presentation.pago


data class DatosTransferencia(
    val banco: String,
    val numeroCuenta: String,
    val nombreTitular: String,
    val referencia: String,
    val monto: Double
)


fun procesarTransferenciaBancaria(datos: DatosTransferencia) {
    // Aquí iría la lógica para procesar la transferencia
    // En una implementación real, esto probablemente enviaría los datos al backend

    // Por ahora solo simulamos el procesamiento
    println("Transferencia procesada: $datos")
}
