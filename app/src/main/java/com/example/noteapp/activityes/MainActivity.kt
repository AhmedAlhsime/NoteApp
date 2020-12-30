package com.example.noteapp.activityes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.MyApp
import com.example.noteapp.R
import com.example.noteapp.ViewModel.NoteViewModel
import com.example.noteapp.ViewModel.NoteViewModelFactory
import com.example.noteapp.adapter.AdapterNote
import com.example.noteapp.data.Injection
import com.example.noteapp.entities.Note
import com.example.noteapp.helper.CustomDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterNote.Callback {
    private lateinit var addNote: ImageView
    private var mNoteViewModel: NoteViewModel? = null
    private val disposable = CompositeDisposable()
    private lateinit var adapterNote: AdapterNote
    private var adapterPosition: Int? = null
    private var note: ArrayList<Note>? = null
    private var resultUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nViewModelFactory: NoteViewModelFactory = Injection.provideViewModelFactory(this)
        mNoteViewModel = ViewModelProvider(
            application as MyApp,
            nViewModelFactory
        ).get(NoteViewModel::class.java)

        addNote = findViewById(R.id.add_note_main)
        addNote.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivity(intent)
        }
        setListNote()
        searchNote()
        quickActions()
        getNote()
    }

    fun getNote() {
        disposable.add(
            mNoteViewModel!!.getAllNote().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { data ->
                    adapterNote.setList(data as ArrayList<Note>)
                    Log.d("getNote", "getNote: $data")
                    note = data
                })
    }

    private fun setListNote() {
        findViewById<RecyclerView>(R.id.rvListNote)
            .run {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapterNote = AdapterNote(this@MainActivity)
                adapter = adapterNote
                isNestedScrollingEnabled = false
            }

    }

    override fun onResume() {
        super.onResume()
        getNote()
    }

    private fun deleteNote(note: Note) {
        disposable.add(mNoteViewModel!!.deletNote(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                adapterNote.deleteItem(adapterPosition!!, note)
            }
        )
    }

    private fun searchNote() {
        searchNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                mNoteViewModel?.searchNote(s.toString())?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : SingleObserver<List<Note>> {
                        override fun onSubscribe(d: Disposable) {
                            disposable.add(d)
                        }

                        override fun onSuccess(t: List<Note>) {
                            if (t.size == 0) {
                                getNote()
                            } else {
                                note?.clear()
                                note?.addAll(t)
                                adapterNote.setList(note!!)
                                adapterNote.notifyDataSetChanged()
                            }
                        }

                        override fun onError(e: Throwable) {
                        }

                    })
            }


        })
    }

    override fun onItemClicked(position: Int, note: Note) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("openNote", note)
        startActivity(intent)
    }

    override fun onItemDeleted(position: Int, note: Note) {
        adapterPosition = position
        dialogDeleteNote(note)
    }

    private fun dialogDeleteNote(note: Note) {
        CustomDialog.alrtAddAndCancelButton(
            this,
            layoutInflater,
            resources.getString(R.string.delete_note),
            resources.getString(R.string.description_delete_note),
            false
        ) { i: Int, s: String ->
            when (i) {
                1 -> {
                    deleteNote(note)
                }
            }
        }

    }

    private fun quickActions() {

        ic_add_note.setOnClickListener {
            val intent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivity(intent)
        }

        ic_add_image.setOnClickListener {
            addImage()
        }

        ic_add_url_web.setOnClickListener {
            dialogAddUrl()
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
                        this@MainActivity,
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
    }

    private fun uploadImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                println("data: ${data}")
                data?.data?.let { uri ->
                    println("uri: $uri")
                    resultUri = uri
                    val intent = Intent(this, CreateNoteActivity::class.java)
                    intent.putExtra("addImage", resultUri)
                    intent.putExtra("icDelete", true)
                    startActivity(intent)
                }
            }
        }
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
                    val intent = Intent(this, CreateNoteActivity::class.java)
                    intent.putExtra("addUri", s)
                    startActivity(intent)
                }
            }
        }
    }
}