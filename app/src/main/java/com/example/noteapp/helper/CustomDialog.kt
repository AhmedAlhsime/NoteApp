package com.example.noteapp.helper

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.noteapp.R


object CustomDialog {

    private var addAndCancelButton: ((Int, String) -> Unit)? = null

    private fun broadCast(buttonId: Int, inputUri: String? = null) {
        addAndCancelButton?.let {
            if (inputUri != null) {
                it(buttonId, inputUri)
            }
        }
    }

    fun alrtAddAndCancelButton(
        context: Context,
        layoutInflater: LayoutInflater,
        title: String,
        description: String,
        inputOrMessage: Boolean,
        buttonClickListener: ((Int, String) -> Unit)
    ) {
        addAndCancelButton = buttonClickListener
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val dialogView: View = layoutInflater.inflate(R.layout.layout_dialog_add_url, null)
        val btnAdd: Button = dialogView.findViewById(R.id.btAdd)
        val btnCancel: Button = dialogView.findViewById(R.id.btCancel)
        val inputUrl: EditText = dialogView.findViewById(R.id.input_url)
        val titleAlrt: TextView = dialogView.findViewById(R.id.text_add_url_dialog)
        val descriptionAlrt: TextView = dialogView.findViewById(R.id.description)

        if (dialogView.parent != null) {
            (dialogView.parent as ViewGroup).removeView(dialogView)
        }
        dialogBuilder
            .setView(dialogView)
            .setCancelable(false)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.background_note
            )
        )

        titleAlrt.setText(title)
        descriptionAlrt.setText(description)
        var uri: String
        when (inputOrMessage) {
            true -> {
                inputUrl.visibility = View.VISIBLE
                val img: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_web)
                titleAlrt.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
            }
            false -> {
                descriptionAlrt.visibility = View.VISIBLE
                val img: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_alert)
                titleAlrt.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
                btnAdd.setText(R.string.confirm)
            }
        }

        dialog.show()
        btnCancel.setOnClickListener {
            // cancel button id = -1
            dialog.dismiss()
            broadCast(-1)
        }
        btnAdd.setOnClickListener {
            // confirm button id = 1
            when (inputOrMessage) {
                true -> {
                    if (inputUrl.text.toString().trim().isEmpty()) {
                        Toast.makeText(context, "Note Url can't be empty", Toast.LENGTH_LONG).show()
                    } else if (inputUrl.text.toString().isNotEmpty()) {
                       uri = inputUrl.text.toString()
                        dialog.dismiss()
                        broadCast(1, uri)
                    }
                }
                false -> {
                    uri = ""
                    dialog.dismiss()
                    broadCast(1,uri)

                }
            }


        }

    }
}


