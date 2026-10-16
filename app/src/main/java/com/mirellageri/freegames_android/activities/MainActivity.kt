package com.mirellageri.freegames_android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirellageri.freegames_android.R
import com.mirellageri.freegames_android.adapters.GameAdapter
import com.mirellageri.freegames_android.data.FreeToGameApi
import com.mirellageri.freegames_android.data.Game
import com.mirellageri.freegames_android.databinding.ActivityMainBinding
import com.mirellageri.freegames_android.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var adapter : GameAdapter
    lateinit var session: SessionManager
    var gameList: List<Game> = emptyList()
    var filteredgameList: List<Game> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //CONFIGURAMOS EL VIEW BINDING
        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)
        //SESSION
        session = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        //ACTION BAR
        val titulo = android.text.SpannableString(getString(R.string.main_title))
        titulo.setSpan(
            android.text.style.ForegroundColorSpan(androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_onPrimary)),
            0, titulo.length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar?.title = titulo
        supportActionBar?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(
                androidx.core.content.ContextCompat.getColor(this,R.color.md_theme_secondaryContainer_highContrast)
            )
        )

        //CONFIGURAMOS EL ADAPTER RECYCLER VIEW
        adapter = GameAdapter(gameList, session){ position ->
            val game = filteredgameList[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_GAME_ID, game.id)
            startActivity(intent)
        }
        binding.recyclerViewMain.adapter = adapter
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)

        //OBTENER LOS JUEGOS DEL API
        val api = FreeToGameApi.getInstance()
        CoroutineScope(Dispatchers.IO).launch{
            gameList = api.getGamesList()
            filteredgameList = gameList

            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(filteredgameList)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        val searchMenuItem = menu.findItem(R.id.menu_search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filteredgameList = gameList.filter {
                    it.title.contains(newText,true)
                }
                adapter.updateData(filteredgameList)
                return true
            }
        })
        return true
    }
}
