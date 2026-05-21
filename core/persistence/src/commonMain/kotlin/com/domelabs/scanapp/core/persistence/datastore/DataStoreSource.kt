package com.domelabs.scanapp.core.persistence.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.domelabs.scanapp.core.persistence.Cached
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

class DataStoreSource(
    private val dataStore: DataStore<Preferences>,
) : Cached {

    fun getBooleanValueFlow(
        key: Preferences.Key<Boolean>,
        default: Boolean? = false,
    ): Flow<Boolean?> = dataStore.data.map { it[key] ?: default }

    suspend fun setBooleanValueFlow(key: Preferences.Key<Boolean>, value: Boolean) =
        dataStore.edit { prefs ->
            prefs[key] = value
        }

    fun getIntValueFlow(key: Preferences.Key<Int>, default: Int): Flow<Int> =
        dataStore.data.map { it[key] ?: default }

    suspend fun setIntValueFlow(key: Preferences.Key<Int>, value: Int) =
        dataStore.edit { prefs ->
            prefs[key] = value
        }

    fun getStringValueFlow(key: Preferences.Key<String>, default: String): Flow<String> =
        dataStore.data.map { it[key] ?: default }

    suspend fun setStringValueFlow(key: Preferences.Key<String>, value: String) =
        dataStore.edit { prefs ->
            prefs[key] = value
        }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val DATA_STORE_FILE_NAME = "dice.preferences_pb"
