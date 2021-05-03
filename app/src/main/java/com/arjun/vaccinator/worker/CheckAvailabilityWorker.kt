package com.arjun.vaccinator.worker

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjun.vaccinator.MainActivity
import com.arjun.vaccinator.R
import com.arjun.vaccinator.RestApi
import com.arjun.vaccinator.model.Session
import com.arjun.vaccinator.util.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@HiltWorker
class CheckAvailabilityWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val restApi: RestApi,
    private val syncManager: SyncManager,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {

        val pincode = inputData.getInt(PINCODE, 110091)
        val age = inputData.getInt(AGE, 18)

        return try {
            val dates = fetchNext10Days()
            dates.forEach { date ->
                val response = restApi.getSlotsForDate(pincode, date)
                val validSlots =
                    response.sessions.filter { slot -> slot.minAgeLimit <= age && slot.availableCapacity > 0 }
                Log.d(TAG, "doWork: Slots: $validSlots")
                if (validSlots.isNullOrEmpty().not()) {
                    sendNotification(validSlots)
                    return Result.success()
                }
                delay(1000)
            }

            syncManager.saveLastSyncDate(LocalDateTime.now().toString())

            Result.failure()

        } catch (e: Exception) {
            Log.d(TAG, "doWork: $e")
            Result.failure()
        }
    }

    private fun fetchNext10Days(): List<String> {
        val dates = mutableListOf<String>()
        var today = LocalDate.now()

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        for (i in 1..10) {
            val dateString = formatter.format(today)
            dates.add(dateString)
            today = today.plusDays(1)
        }
        return dates
    }

    private fun sendNotification(sessions: List<Session>) {
        val id = sessions.hashCode()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = "Vaccination Slots Available"
        val subtitleNotification = sessions.joinToString(separator = "\n") {
            "${it.name} has ${it.availableCapacity} slots available."
        }
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_virus)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.ic_virus
                )
            )
            .setContentTitle(titleNotification)
            .setContentText(subtitleNotification)
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.priority = PRIORITY_MAX

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }

    companion object {
        private const val TAG = "CheckAvailabilityWorker"
        const val PINCODE = "pincode"
        const val AGE = "age"
        private val NOTIFICATION_ID = UUID.randomUUID().toString()
        private val NOTIFICATION_CHANNEL = UUID.randomUUID().toString()
        private const val NOTIFICATION_NAME = "Vaccination Appointment"
    }
}