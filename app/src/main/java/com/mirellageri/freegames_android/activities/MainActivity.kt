package com.mirellageri.freegames_android.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mirellageri.freegames_android.R
import com.mirellageri.freegames_android.adapters.GameAdapter
import com.mirellageri.freegames_android.data.FreeToGameApi
import com.mirellageri.freegames_android.data.Game
import com.mirellageri.freegames_android.databinding.ActivityMainBinding
import com.mirellageri.freegames_android.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var adapter : GameAdapter
    lateinit var session: SessionManager
    lateinit var bottomNavigationView: BottomNavigationView
    var gameList: List<Game> = emptyList()
    var filteredGameList: List<Game> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //CONFIGURAMOS EL VIEW BINDING E INICIALIZAR NAVIGATION
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationView = binding.bottomNavigationView
        //SESSION
        session = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets

        }

        //ACTION BAR
        val title = android.text.SpannableString(getString(R.string.main_title))
        title.setSpan(
            android.text.style.ForegroundColorSpan(androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_onPrimary)),
            0, title.length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar?.title = title
        supportActionBar?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(
                androidx.core.content.ContextCompat.getColor(this,R.color.md_theme_secondaryContainer_highContrast)
            )
        )

        //CONFIGURAMOS EL ADAPTER RECYCLER VIEW
        adapter = GameAdapter(gameList){ position ->
            val game = filteredGameList[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_GAME_ID, game.id)
            startActivity(intent)
        }
        binding.recyclerViewMain.adapter = adapter
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)

        //OBTENER LOS JUEGOS DEL API

        getNavigationView()
        getFreeGames()
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
                filteredGameList = gameList.filter {
                    it.title.contains(newText,true)
                }
                adapter.updateData(filteredGameList)
                return true
            }
        })
        return true
    }
    private fun getFreeGames(){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val api = FreeToGameApi.getInstance()
                gameList = api.getGamesList()
                filteredGameList = gameList

                CoroutineScope(Dispatchers.Main).launch {
                    adapter.updateData(filteredGameList)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private fun getNavigationView(){
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_game -> {
                    filteredGameList = gameList
                    adapter.updateData(filteredGameList)
                    true
                }
                R.id.menu_library -> {
                    showFavoriteGames()
                    true
                }
                else -> false
            }
        }
    }
    private fun showFavoriteGames(){
        val favoriteIds = session.getFavorites()
        filteredGameList = gameList.filter { game->
            game.id.toString() in favoriteIds
        }
        adapter.updateData(filteredGameList)
    }
}
