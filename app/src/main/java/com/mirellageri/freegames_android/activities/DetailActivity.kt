package com.mirellageri.freegames_android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mirellageri.freegames_android.R
import com.mirellageri.freegames_android.adapters.ScreenshotAdapter
import com.mirellageri.freegames_android.data.FreeToGameApi
import com.mirellageri.freegames_android.data.Game
import com.mirellageri.freegames_android.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    companion object{
        //ESTE ES PARA QUE NOS NOS CONFUNDAMOS A LA HORA DE ESCRIBIR LA LLAVE QUE LOS VINCULA
        const val EXTRA_GAME_ID = "GAME_ID"
    }
    lateinit var binding: ActivityDetailBinding
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //CONFIGURAR BINDING
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //OBTENER EL ID DE MAIN
        val id = intent.getIntExtra(EXTRA_GAME_ID,-1)
        //OBTENER EL JUEGO DEL API CON ID
        val api = FreeToGameApi.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            game = api.getGameById(id)
            CoroutineScope(Dispatchers.Main).launch{
                //Cargar loadData
                loadData()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun loadData(){
        supportActionBar?.title = game.title
        //supportActionBar?.subtitle = game.developer

        //HERO IMAGE
        Picasso.get().load(game.thumbnail).into(binding.thumbnailImageView)
        //BASIC INFO
        binding.titleTextView.text = game.title
        binding.genreChip.text = game.genre
        Log.i("API REST",game.platform)
        when(game.platform){
            "PC (Windows), Web Browser" ->{
                binding.platformPCImageView.setImageResource(R.drawable.ic_platform_pc)
                binding.platformPCImageView.visibility = View.VISIBLE
                binding.platformWebImageView.setImageResource(R.drawable.ic_platform_web)
                binding.platformWebImageView.visibility = View.VISIBLE
            }
            "PC (Windows)"->  {
                binding.platformPCImageView.setImageResource(R.drawable.ic_platform_pc)
                binding.platformPCImageView.visibility = View.VISIBLE
                binding.platformWebImageView.visibility = View.GONE
            }
            "Web Browser" -> {
                binding.platformWebImageView.setImageResource(R.drawable.ic_platform_web)
                binding.platformWebImageView.visibility = View.VISIBLE
                binding.platformPCImageView.visibility = View.GONE
            }
        }
        //DESCRIPTION
        binding.descriptionTextView.text = game.description
        // Screenshots
        val adapter = ScreenshotAdapter(game.screenshots ?: emptyList())
        binding.screenshotsRecyclerView.adapter = adapter

        // System requirements
        binding.systemRequirementsOsTextView.text = game.systemRequirements?.os
        binding.systemRequirementsProcessorTextView.text = game.systemRequirements?.processor
        binding.systemRequirementsMemoryTextView.text = game.systemRequirements?.memory
        binding.systemRequirementsGraphicsTextView.text = game.systemRequirements?.graphics
        binding.systemRequirementsStorageTextView.text = game.systemRequirements?.storage

        // Buttons
        binding.playButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(game.gameUrl.toUri())
            startActivity(intent)
        }
        binding.shareButton.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mira que juego está gratis: ${game.profileUrl}.")
            sendIntent.type = "text/plain"

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}