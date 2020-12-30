package com.example.noteapp.data

import android.content.Context
import com.example.noteapp.ViewModel.NoteViewModelFactory
import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.deo.NoteDeo

object Injection {


    fun provideNoteDataSource(context: Context) : NoteDeo{
        val db = NoteDatabase.getInstance(context)
        return db.noteDeo()
    }


    fun provideViewModelFactory(context: Context): NoteViewModelFactory{
        val dataSource = provideNoteDataSource(context)
        return NoteViewModelFactory(dataSource)
    }
}