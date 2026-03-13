package org.christophertwo.car.feature.parking.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.context.GlobalContext

actual fun getDatabaseBuilder(): RoomDatabase.Builder<CarLocateDatabase> {
    val context = GlobalContext.get().get<Context>()
    val dbFile = context.getDatabasePath("carlocate.db")
    return Room.databaseBuilder<CarLocateDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
}

