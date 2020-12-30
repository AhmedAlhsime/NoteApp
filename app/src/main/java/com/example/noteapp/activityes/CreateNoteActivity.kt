package com.example.noteapp.activityes

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.noteapp.MyApp
import com.example.noteapp.R
import com.example.noteapp.ViewModel.NoteViewModel
import com.example.noteapp.ViewModel.NoteViewModelFactory
import com.example.noteapp.data.Injection
import com.example.noteapp.entities.Note
import com.example.noteapp.helper.CustomDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.layout_dialog_add_url.*
import kotlinx.android.synthetic.main.layout_persistent_bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*


class CreateNoteActivity : AppCompatActivity() {
    private lateinit var iconBack: ImageView
    private lateinit var title: EditText
    private lateinit var subTitle: EditText
    private lateinit var noteText: EditText
    private lateinit var dataTime: TextView
    private lateinit var saveNote: ImageView
    private var note: Note? = null
    private var updateNote: Note? = null
    private var mNoteViewModel: NoteViewModel? = null
    private val disposable = CompositeDisposable()
    private lateinit var layoutBottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var selecteColorNote = "#333333"
    private var resultUri: Uri? = null
    private var ic_delete_image: Boolean = false
    private lateinit var textUrl: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val nVMFactoryAppNote: NoteViewModelFactory = Injection.provideViewModelFactory(this)
        mNoteViewModel = ViewModelProvider(
            application as MyApp,
            nVMFactoryAppNote
        ).get(NoteViewModel::class.java)
        iconBack = findViewById(R.id.ic_back)
        title = findViewById(R.id.input_title)
        subTitle = findViewById(R.id.input_subTitle)
        noteText = findViewById(R.id.input_note)
        dataTime = findViewById(R.id.text_data_time)
        saveNote = findViewById(R.id.ic_save)
        layoutBottomSheet = findViewById(R.id.bottom_sheet)
        textUrl = findViewById(R.id.textUrl)
        getIntents()
        clickListeners()
        bottomSheet()
        setColorIndicator()
        viewIconDeleteUrl()

    }

    private fun saveNote() {
        if (title.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty", Toast.LENGTH_LONG).show()
            return
        } else if (subTitle.text.toString().trim().isEmpty() || noteText.text.toString().trim()
                .isEmpty()
        ) {
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_LONG).show()
            return
        } else {
            if (updateNote?.id != null) {
                note = Note(
                    id = updateNote?.id,
                    titel = title.text.toString(),
                    subtitle = subTitle.text.toString(),
                    note_text = noteText.text.toString(),
                    data_time = dataTime.text.toString(),
                    color = selecteColorNote,
                    image_path = resultUri,
                    url = textUrl.text.toString(),
                    ic_delete_image = ic_delete_image
                )
                insertNote(note!!)
            } else {
                note = Note(
                    titel = title.text.toString(),
                    subtitle = subTitle.text.toString(),
                    note_text = noteText.text.toString(),
                    data_time = dataTime.text.toString(),
                    color = selecteColorNote,
                    image_path = resultUri,
                    url = textUrl.text.toString(),
                    ic_delete_image = ic_delete_image
                )
                insertNote(note!!)

            }

        }

    }

    private fun insertNote(note: Note) {
        disposable.add(mNoteViewModel!!.insertDB(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onBackPressed()
            }
        )
    }

    private fun bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    setNoteColorIndicator()
                    bottomSheet.hideKeyboard()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        layoutBottomSheet.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        ic_add_image_bottom_sheet.setOnClickListener {
            addImage()
        }

        ic_add_url_bottom_sheet.setOnClickListener {
            dialogAddUrl()
        }

        ic_copy.setOnClickListener {
            copyTextNote()
        }

        delete_note.setOnClickListener {
            dialogDeleteNote()
        }

    }

    private fun setNoteColorIndicator() {
        noteDefault1.setOnClickListener {
            ic_done_note1.visibility = View.VISIBLE
            ic_done_note2.visibility = View.GONE
            ic_done_note3.visibility = View.GONE
            ic_done_note4.visibility = View.GONE
            ic_done_note5.visibility = View.GONE
            selecteColorNote = "#333333"
            setColorIndicator()
        }
        note2.setOnClickListener {
            ic_done_note2.visibility = View.VISIBLE
            ic_done_note1.visibility = View.GONE
            ic_done_note3.visibility = View.GONE
            ic_done_note4.visibility = View.GONE
            ic_done_note5.visibility = View.GONE
            selecteColorNote = "#FDBE3B"
            setColorIndicator()
        }
        note3.setOnClickListener {
            ic_done_note3.visibility = View.VISIBLE
            ic_done_note1.visibility = View.GONE
            ic_done_note2.visibility = View.GONE
            ic_done_note4.visibility = View.GONE
            ic_done_note5.visibility = View.GONE
            selecteColorNote = "#2196F3"
            setColorIndicator()
        }
        note4.setOnClickListener {
            ic_done_note4.visibility = View.VISIBLE
            ic_done_note1.visibility = View.GONE
            ic_done_note3.visibility = View.GONE
            ic_done_note2.visibility = View.GONE
            ic_done_note5.visibility = View.GONE
            selecteColorNote = "#3A52Fc"
            setColorIndicator()
        }
        note5.setOnClickListener {
            ic_done_note5.visibility = View.VISIBLE
            ic_done_note1.visibility = View.GONE
            ic_done_note3.visibility = View.GONE
            ic_done_note4.visibility = View.GONE
            ic_done_note2.visibility = View.GONE
            selecteColorNote = "#FFFFFF"
            setColorIndicator()
        }

        when (selecteColorNote) {
            "#333333" -> {
                ic_done_note1.visibility = View.VISIBLE
                ic_done_note2.visibility = View.GONE
                ic_done_note3.visibility = View.GONE
                ic_done_note4.visibility = View.GONE
                ic_done_note5.visibility = View.GONE
            }
            "#FDBE3B" -> {
                ic_done_note2.visibility = View.VISIBLE
                ic_done_note1.visibility = View.GONE
                ic_done_note3.visibility = View.GONE
                ic_done_note4.visibility = View.GONE
                ic_done_note5.visibility = View.GONE
            }
            "#2196F3" -> {
                ic_done_note3.visibility = View.VISIBLE
                ic_done_note1.visibility = View.GONE
                ic_done_note2.visibility = View.GONE
                ic_done_note4.visibility = View.GONE
                ic_done_note5.visibility = View.GONE
            }
            "#3A52Fc" -> {
                ic_done_note4.visibility = View.VISIBLE
                ic_done_note1.visibility = View.GONE
                ic_done_note3.visibility = View.GONE
                ic_done_note2.visibility = View.GONE
                ic_done_note5.visibility = View.GONE
            }
            "#FFFFFF" -> {
                ic_done_note5.visibility = View.VISIBLE
                ic_done_note1.visibility = View.GONE
                ic_done_note3.visibility = View.GONE
                ic_done_note4.visibility = View.GONE
                ic_done_note2.visibility = View.GONE
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setColorIndicator() {
        val drawable = view_indicator.background as Drawable
        val color = Color.parseColor(selecteColorNote)
        drawable.setColorFilter(color, PorterDuff.Mode.SRC)
    }

    private fun uploadImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                println("data: ${data}")
                data?.data?.let { uri ->
                    println("uri: $uri")
                    resultUri = uri
                    setImage(resultUri)
                    ic_delete_image = true
                }
            }
        }
    }


    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun dialogAddUrl() {
        CustomDialog.alrtAddAndCancelButton(
            this,
            layoutInflater,
            resources.getString(R.string.add_url_dialog),
            "",
            true
        ) { i: Int, s: String ->
            when (i) {
                1 -> {
                    textUrl.setText(s)
                    Log.d("textUrl", "dialog: ${textUrl.text.toString()}")
                    textUrl.visibility = View.VISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

    }

    private fun dialogDeleteNote() {
        CustomDialog.alrtAddAndCancelButton(
            this,
            layoutInflater,
            resources.getString(R.string.delete_note),
            resources.getString(R.string.description_delete_note),
            false
        ) { i: Int, s: String ->
            when (i) {
                1 -> {
                    deleteNote(updateNote!!)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

    }

    private fun dialogDeleteUrl() {
        CustomDialog.alrtAddAndCancelButton(
            this,
            layoutInflater,
            resources.getString(R.string.delete_url),
            resources.getString(R.string.description_delete_url),
            false
        ) { i: Int, s: String ->
            when (i) {
                1 -> {
                    textUrl.text = null
                    textUrl.visibility = View.GONE
                    delete_uri.visibility = View.GONE
                }
            }
        }

    }

    private fun dialogDeleteImage() {
        CustomDialog.alrtAddAndCancelButton(
            this,
            layoutInflater,
            resources.getString(R.string.delete_image),
            resources.getString(R.string.description_delete_image),
            false
        ) { i: Int, s: String ->
            when (i) {
                1 -> {
                    resultUri = null
                    setImage(resultUri)
                    delete_image.visibility = View.GONE
                }
            }
        }

    }

    private fun addImage() {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    uploadImage()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(
                        this@CreateNoteActivity,
                        "Permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun copyTextNote() {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Text Note", noteText.text.toString())
        if (clipBoard != null && clipData != null && noteText.text.toString().trim().isNotEmpty()) {
            clipBoard.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied note ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNote() {
        updateNote?.let {
            title.setText(it.titel)
            subTitle.setText(it.subtitle)
            noteText.setText(it.note_text)
            setImage(it.image_path)
            resultUri = it.image_path
            selecteColorNote = it.color!!
            if (it.url != null) {
                textUrl.setText(it.url)
                textUrl.visibility = View.VISIBLE
            }
            dataTime.setText(it.data_time)
            delete_note.visibility = View.VISIBLE
            if (it.ic_delete_image == true) {
                delete_image.visibility = View.VISIBLE
            }
        }

    }

    private fun deleteNote(note: Note) {
        disposable.add(mNoteViewModel!!.deletNote(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onBackPressed()
            }
        )
    }

    private fun setImage(uri: Uri?) {
        Glide.with(imageNote)
            .load(uri)
            .into(imageNote)
    }

    private fun viewIconDeleteUrl() {
        if (textUrl.text.toString().isNotBlank()) {
            delete_uri.visibility = View.VISIBLE
        } else {
            delete_uri.visibility = View.GONE
        }
    }

    private fun getIntents() {
        intent.getParcelableExtra<Note>("openNote").let {
            if (it != null) {
                updateNote = it
                updateNote()
            }
        }

        ic_delete_image = intent.getBooleanExtra("icDelete", false)
        intent.getParcelableExtra<Uri>("addImage").let {
            if (it != null) {
                resultUri = it
                setImage(resultUri)
                delete_image.visibility = View.VISIBLE
            }
        }

        intent.getStringExtra("addUri").let {
            if (it != null) {
                textUrl.text = it
                textUrl.visibility = View.VISIBLE
            }
        }

    }

    private fun clickListeners() {
        iconBack.setOnClickListener {
            onBackPressed()
            it.hideKeyboard()
        }

        dataTime.setText(
            SimpleDateFormat("EEEE, dd/MM/yyyy-HH:MM a", Locale.getDefault())
                .format(Date())
        )

        delete_uri.setOnClickListener {
            dialogDeleteUrl()
        }
        delete_image.setOnClickListener {
            dialogDeleteImage()
        }

        saveNote.setOnClickListener {
            it.hideKeyboard()
            saveNote()
        }
    }

}

