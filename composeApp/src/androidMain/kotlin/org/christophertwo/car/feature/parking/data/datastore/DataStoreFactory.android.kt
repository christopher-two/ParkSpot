package org.christophertwo.car.feature.parking.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import org.koin.core.context.GlobalContext

actual fun createDataStore(): DataStore<Preferences> {
    val context = GlobalContext.get().get<Context>()
    return context.preferencesDataStore
}

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_FILE_NAME
)

