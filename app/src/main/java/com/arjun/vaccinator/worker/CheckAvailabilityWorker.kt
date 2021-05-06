package com.arjun.vaccinator.worker

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.graphics.Color.BLUE
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjun.vaccinator.CoWinApi
import com.arjun.vaccinator.MainActivity
import com.arjun.vaccinator.R
import com.arjun.vaccinator.model.District
import com.arjun.vaccinator.model.Session
import com.arjun.vaccinator.util.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@HiltWorker
class CheckAvailabilityWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val coWinApi: CoWinApi,
    private val syncManager: SyncManager,
) : CoroutineWorker(appContext, workerParameters) {

    private val notificationManager by lazy {
        applicationContext.getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
    }

    override suspend fun doWork(): Result {

        var gotAppointment = false
        val dates = fetchNext15Days()

        return try {

            val districts = getAllDistricts(9)

            districts.forEach { district ->
                dates.forEach { date ->
                    val response = coWinApi.getSlotsByDistrict(
                        districtId = district.districtId,
                        date = date
                    )
                    val validSlots =
                        response.sessions.filter { slot -> slot.minAgeLimit <= AGE && slot.vaccine.toLowerCase() == "covaxin" && slot.availableCapacity > 0 }
                    Timber.d("doWork: Slots: $validSlots")
                    if (validSlots.isNullOrEmpty().not()) {
                        gotAppointment = true
                        notifyAboutAvailableSlots(validSlots)
                    }
                    delay(1000)
                }
                delay(1000)
            }

            syncManager.saveLastSyncDate(LocalDateTime.now().toString())

            if (gotAppointment.not())
                notifyAboutNonAvailableSlots()

            Result.success()

        } catch (e: Exception) {
            Timber.d("doWork: $e")
            notifyAboutError(e.message)
            Result.failure()
        }
    }

    private suspend fun getAllDistricts(stateId: Int): List<District> {
        val response = coWinApi.getAllDistrictOfTheState(stateId)
        return response.districts
    }

    private fun fetchNext15Days(): List<String> {
        val dates = mutableListOf<String>()
        var today = LocalDate.now()

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        for (i in 1..15) {
            val dateString = formatter.format(today)
            dates.add(dateString)
            today = today.plusDays(1)
        }
        return dates
    }

    private fun notifyAboutAvailableSlots(sessions: List<Session>) {
        val id = sessions.hashCode()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)


        val titleNotification = "Vaccination Slots Available For ${sessions.first().date}"
        val subtitleNotification = "At ${sessions.size} centres"
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        val style = NotificationCompat.BigTextStyle()
            .bigText(sessions.joinToString(separator = "\n") {
                "${it.name} has ${it.availableCapacity} slots available for ${it.vaccine.toUpperCase()} District ${it.districtName}"
            })

        sendNotification(id, titleNotification, subtitleNotification, pendingIntent, style)

    }

    private fun notifyAboutNonAvailableSlots() {
        val id = (0..Int.MAX_VALUE).random()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val titleNotification = "No Vaccine Available"
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)

        sendNotification(
            id = id,
            titleNotification = titleNotification,
            pendingIntent = pendingIntent
        )

    }

    private fun notifyAboutError(subtitleNotification: String?) {
        val id = (0..Int.MAX_VALUE).random()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val titleNotification = "Something went wrong"
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)

        sendNotification(
            id = id,
            titleNotification = titleNotification,
            subtitleNotification = subtitleNotification,
            pendingIntent = pendingIntent
        )

    }

    private fun sendNotification(
        id: Int,
        titleNotification: String,
        subtitleNotification: String? = null,
        pendingIntent: PendingIntent,
        style: NotificationCompat.Style? = null
    ) {
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_virus)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    applicationContext.resources,
                    R.drawable.ic_virus
                )
            )
            .setStyle(style)
            .setContentTitle(titleNotification)
            .setContentText(subtitleNotification)
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.priority = PRIORITY_MAX

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes =
                AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    NOTIFICATION_NAME,
                    IMPORTANCE_HIGH
                )

            channel.enableLights(true)
            channel.lightColor = BLUE
            channel.enableVibration(true)
            channel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }

    companion object {
        const val AGE = 18
        private val NOTIFICATION_ID = UUID.randomUUID().toString()
        private val NOTIFICATION_CHANNEL = UUID.randomUUID().toString()
        private const val NOTIFICATION_NAME = "Vaccination Appointment"
    }
}