package com.arjun.vaccinator.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(
    name = "user_pref"
)

class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveLastSyncDate(lastRefreshDate: String) {
        context.dataStore.edit {
            it[ID_LAST_REFRESH_DATE] = lastRefreshDate
        }
    }

    fun getLastSyncDate(): Flow<String> = context.dataStore.data.map {
        it[ID_LAST_REFRESH_DATE] ?: ""
    }

    companion object {
        private val ID_LAST_REFRESH_DATE = stringPreferencesKey("id_last_refresh_date")
    }
}