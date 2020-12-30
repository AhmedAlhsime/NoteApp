package com.example.noteapp.ViewModel

import androidx.lifecycle.ViewModel
import com.example.noteapp.deo.NoteDeo
import com.example.noteapp.entities.Note
import io.reactivex.Completable
import io.reactivex.Single

class NoteViewModel(private val dataSource: NoteDeo) : ViewModel() {


    fun insertDB(
        note: Note,
    ): Completable {

        return dataSource.insertNote(note)
    }

    fun getAllNote(): Single<List<Note>> {
        return dataSource.getAllNotes()
    }

    fun deletNote(note: Note): Completable {
        return dataSource.deleteNote(note)
    }

    fun searchNote(keyword: String): Single<List<Note>> {
        return dataSource.searchNotes(keyword)
    }
}