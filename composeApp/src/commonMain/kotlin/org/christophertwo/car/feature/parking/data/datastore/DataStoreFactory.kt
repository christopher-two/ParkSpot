package org.christophertwo.car.feature.parking.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(): DataStore<Preferences>

internal const val DATA_STORE_FILE_NAME = "carlocate.preferences_pb"

