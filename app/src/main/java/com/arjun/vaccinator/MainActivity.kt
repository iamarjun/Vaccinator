package com.arjun.vaccinator

import android.os.Bundle
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
                .setInputData(
                    workDataOf(
                        CheckAvailabilityWorker.PINCODE to 110091,
                        CheckAvailabilityWorker.AGE to 25
                    )
                )
                .setConstraints(constraints)
                .build()

        workManager.enqueue(checkAvailabilityRequest)

        syncManager.getLastSyncDate().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                binding.lastRefreshDate.text = it
            }.launchIn(lifecycleScope)

    }

    companion object {
        private const val TAG = "MainActivity"
        private const val OUTPUT = "output"
    }
}