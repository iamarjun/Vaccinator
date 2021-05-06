package com.arjun.vaccinator

import android.os.Bundle
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.arjun.vaccinator.databinding.ActivityMainBinding
import com.arjun.vaccinator.util.SyncManager
import com.arjun.vaccinator.util.viewBinding
import com.arjun.vaccinator.worker.CheckAvailabilityWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val workManager by lazy { WorkManager.getInstance(this) }
    private val binding by viewBinding(ActivityMainBinding::inflate)

    @Inject
    internal lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val checkAvailabilityRequest =
            PeriodicWorkRequestBuilder<CheckAvailabilityWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()


        workManager.enqueueUniquePeriodicWork(
            SYNC_DATA_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            checkAvailabilityRequest
        )

        syncManager.getLastSyncDate().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {

                if (it.isEmpty())
                    return@onEach

                binding.lastRefreshDate.text = DateUtils.getRelativeTimeSpanString(
                    LocalDateTime.parse(it).toEpochSecond(
                        ZoneOffset.UTC
                    ),
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    DateUtils.MINUTE_IN_MILLIS
                )
            }.launchIn(lifecycleScope)

    }

    companion object {
        private const val SYNC_DATA_WORK_NAME = "SYNC_DATA_WORK_NAME"
    }
}