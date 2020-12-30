package com.example.noteapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noteapp.R
import com.example.noteapp.entities.Note

class AdapterNote(
    val callback: Callback
) : RecyclerView.Adapter<AdapterNote.NoteViewHolder>() {
    private var noteList = ArrayList<Note>()
    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        context = parent.context
        return NoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_note, parent, false), callback
        )
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        noteList.get(position).let { (holder as NoteViewHolder).bind(it) }
        if (noteList[position].color!!.endsWith("#FFFFFF")) {
            holder.titelNote.setTextColor(Color.parseColor("#000000"))
            holder.subtitleNote.setTextColor(Color.parseColor("#000000"))
            holder.dataNote.setTextColor(Color.parseColor("#000000"))
        } else {
            holder.titelNote.setTextColor(Color.parseColor("#FFFFFF"))
            holder.subtitleNote.setTextColor(Color.parseColor("#FFFFFF"))
            holder.dataNote.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    fun deleteItem(position: Int, item: Note) {
        noteList.remove(item)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, noteList.size)
    }

    fun setList(noteList: ArrayList<Note>) {
        this.noteList = noteList
        notifyDataSetChanged()
    }

    class NoteViewHolder(
        private val myItem: View,
        private val callback: Callback

    ) : RecyclerView.ViewHolder(myItem) {

        val cardNote = itemView.findViewById<CardView>(R.id.cardNote)
        var titelNote = itemView.findViewById<TextView>(R.id.titleNote)
        val subtitleNote = itemView.findViewById<TextView>(R.id.subtitleNote)
        val dataNote = itemView.findViewById<TextView>(R.id.textDataTimeNote)
        val image_list_note = itemView.findViewById<ImageView>(R.id.image_list_note)
        val urlItem = itemView.findViewById<TextView>(R.id.urlItem)
        val ic_delete_note_list = itemView.findViewById<ImageView>(R.id.ic_delete_note_list)

        fun bind(item: Note) {
            item.let {
                titelNote.text = it.titel
                subtitleNote.text = it.subtitle
                dataNote.text = it.data_time
                var color = it.color
                cardNote.setCardBackgroundColor(Color.parseColor("$color"))
                Glide.with(image_list_note)
                    .load(item.image_path)
                    .into(image_list_note)
                if (item.url != null) {
                    urlItem.text = it.url
                    urlItem.visibility = View.VISIBLE
                } else {
                    urlItem.visibility = View.GONE
                }
                ic_delete_note_list.setOnClickListener {
                    callback.onItemDeleted(adapterPosition, item)
                }

                myItem.setOnClickListener {
                    callback.onItemClicked(adapterPosition, item)
                }
            }
        }
    }

    interface Callback {
        fun onItemClicked(position: Int, note: Note)
        fun onItemDeleted(position: Int, note: Note)
    }


}