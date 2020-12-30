package com.example.noteapp.entities

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "titel")
    var titel: String? = null,

    @ColumnInfo(name = "data_time")
    var data_time: String? = null,

    @ColumnInfo(name = "subtitle")
    var subtitle: String? = null,

    @ColumnInfo(name = "note_text")
    var note_text: String? = null,

    @ColumnInfo(name = "image_path")
    var image_path: Uri? = null,

    @ColumnInfo(name = "color")
    var color: String? = null,

    @ColumnInfo(name = "url")
    var url: String? = null,

    @ColumnInfo(name = "ic_delete_image")
    var ic_delete_image: Boolean? = null
) : Parcelable
