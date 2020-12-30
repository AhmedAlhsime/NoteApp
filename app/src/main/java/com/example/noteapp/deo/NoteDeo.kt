package com.example.noteapp.deo

import androidx.room.*
import com.example.noteapp.entities.Note
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface NoteDeo {

    @Query("SELECT * FROM NOTES ORDER BY id DESC")
    fun getAllNotes(): Single<List<Note>>

    @Query("SELECT * FROM NOTES WHERE titel LIKE :keyword OR subtitle LIKE :keyword OR note_text LIKE :keyword")
    fun searchNotes(keyword :String): Single<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertNote(note: Note) : Completable

    @Delete
    fun deleteNote(note: Note) : Completable
}