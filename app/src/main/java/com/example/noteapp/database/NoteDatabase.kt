package com.example.noteapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.noteapp.deo.NoteDeo
import com.example.noteapp.entities.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(UriConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDeo(): NoteDeo

    companion object {

        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java, "note.db"
            ).fallbackToDestructiveMigration()
                .build()
    }

}