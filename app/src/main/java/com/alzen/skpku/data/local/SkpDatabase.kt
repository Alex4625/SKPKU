package com.alzen.skpku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SkpEntity::class], version = 1, exportSchema = false)
abstract class SkpDatabase : RoomDatabase() {
    abstract fun skpDao(): SkpDao
}
