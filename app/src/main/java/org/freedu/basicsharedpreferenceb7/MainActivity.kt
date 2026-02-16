package org.freedu.basicsharedpreferenceb7

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.freedu.basicsharedpreferenceb7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences

    private val noteList = mutableListOf<Note>()
    private lateinit var adapter: NoteAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPref = getSharedPreferences("notes_pref", MODE_PRIVATE)

        adapter = NoteAdapter(noteList)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.adapter = adapter

        // Load saved text into EditText
        binding.editNote.setText(sharedPref.getString("draft_text", ""))

        binding.editNote.addTextChangedListener {
            sharedPref.edit { putString("draft_text", it.toString()) }
        }

        loadNotes()


        // FAB Click
        binding.btnAdd.setOnClickListener {

            val text = binding.editNote.text.toString()

            if (text.isNotEmpty()) {

                noteList.add(Note(text))

                saveNotes()

                adapter.notifyDataSetChanged()

                binding.editNote.text?.clear()

                sharedPref.edit { remove("draft_text") }
            }
        }
    }

    private fun saveNotes() {

        val gson = Gson()
        val json = gson.toJson(noteList)

        sharedPref.edit {
            putString("notes", json)
        }
    }

    private fun loadNotes() {

        val gson = Gson()
        val json = sharedPref.getString("notes", null)

        val type = object : TypeToken<MutableList<Note>>() {}.type

        val savedList: MutableList<Note>? = gson.fromJson(json, type)

        if (savedList != null) {
            noteList.addAll(savedList)
        }
    }
}