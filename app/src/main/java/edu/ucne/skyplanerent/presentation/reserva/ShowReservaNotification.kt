package edu.ucne.skyplanerent.presentation.reserva

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import edu.ucne.skyplanerent.MainActivity
import edu.ucne.skyplanerent.R


@androidx.annotation.RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showReservaNotification(
    context: Context,
    reservaId: Int,
    origen: String,
    destino: String,
    fecha: String,
    precioTotal: Double,

) {


    // 1. Configuración del canal (requerido para Android 8.0+)
    val channelId = "reserva_channel_$reservaId"
    val channelName = "Confirmaciones de Reserva"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH // Prioridad alta
        ).apply {
            description = "Notificaciones de confirmación de reservas aéreas"
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 100, 200)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // 2. Crear Intent para abrir la app al hacer click
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    // 3. Construir notificación con estilo expandible
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logoskyfleet) // Icono personalizado
        .setColor(ContextCompat.getColor(context, R.color.purple_700))
        .setContentTitle("✅ Reserva Confirmada #$reservaId")
        .setContentText("$origen → $destino - $fecha")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("""
                Reserva confirmada exitosamente
                ✈️ Vuelo: $origen → $destino
                📅 Fecha: $fecha
                💰 Total: ${"%.2f".format(precioTotal)} RD$
                """.trimIndent())
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .addAction(
            R.drawable.iconinfo,
            "Ver Detalles",
            pendingIntent
        )
        .build()

    // 4. Mostrar notificación
    with(NotificationManagerCompat.from(context)) {
        try {
            notify(reservaId, notification)
        } catch (e: Exception) {
            Log.e("Notification", "Error al mostrar notificación", e)
        }
    }
}


@androidx.annotation.RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showReservaDeletedNotification(
    context: Context,
    reservaId: Int,
    origen: String,
    destino: String,
    fecha: String,
    precioTotal: Double
) {
    // Check if we have permission (only required for API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Notification", "Notification permission not granted")
            return
        }
    }

    // Rest of your existing notification code...
    val channelId = "reserva_channel_$reservaId"
    val channelName = "Cancelaciones de Reserva"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de cancelación de reservas aéreas"
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 100, 200)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logoskyfleet)
        .setColor(ContextCompat.getColor(context, R.color.purple_700))
        .setContentTitle("❌ Reserva Cancelada #$reservaId")
        .setContentText("$origen → $destino - $fecha")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("""
                Su reserva ha sido cancelada
                ✈️ Vuelo: $origen → $destino
                📅 Fecha: $fecha
                💰 Total reembolsable: ${"%.2f".format(precioTotal)} RD$
                
                Consulte nuestra política de cancelación
                """.trimIndent())
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .addAction(
            R.drawable.iconinfo,
            "Ver Política",
            pendingIntent
        )
        .build()

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(reservaId + 1000, notification)
        } catch (e: Exception) {
            Log.e("Notification", "Error al mostrar notificación de cancelación", e)
        }
    }
}