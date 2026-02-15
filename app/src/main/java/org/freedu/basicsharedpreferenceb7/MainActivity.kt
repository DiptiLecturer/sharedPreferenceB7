package org.freedu.basicsharedpreferenceb7

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.freedu.basicsharedpreferenceb7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var adapter: NoteAdapter
    private val noteList = mutableListOf<Note>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("notes_pref", MODE_PRIVATE)

        adapter= NoteAdapter(noteList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter=adapter



        //load data
        loadNotes()


        binding.btnSave.setOnClickListener {
            val note = binding.editNote.text.toString()

            if(note.isNotEmpty()){
                noteList.add(Note(note))
                saveNote()
                adapter.notifyDataSetChanged()
                binding.editNote.text.clear()
            }
        }

    }
    //save the data
    fun saveNote(){
        val gson = Gson()
        val json = gson.toJson(noteList)

        sharedPref.edit()
            .putString("notes",json)
            .apply()
    }
    //load the data

    private fun loadNotes() {

        val gson = Gson()

        val json = sharedPref.getString("notes",null)

        val type = object : TypeToken<MutableList<Note>>() {}.type

        val savedList: MutableList<Note>? = gson.fromJson(json,type)

        if(savedList!=null){
            noteList.addAll(savedList)
        }
    }
}