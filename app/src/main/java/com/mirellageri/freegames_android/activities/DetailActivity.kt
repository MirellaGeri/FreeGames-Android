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
import com.mirellageri.freegames_android.adapters.GalleryAdapter
import com.mirellageri.freegames_android.data.FreeToGameApi
import com.mirellageri.freegames_android.data.Game
import com.mirellageri.freegames_android.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class DetailActivity : AppCompatActivity() {
    companion object{
        //ESTE ES PARA QUE NOS NOS CONFUNDAMOS A LA HORA DE ESCRIBIR LA LLAVE QUE LOS VINCULA
        const val EXTRA_GAME_ID = "GAME_ID"
    }
    lateinit var binding: ActivityDetailBinding
    lateinit var game: Game
    var showMoreEnabled = false

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
        CoroutineScope(Dispatchers.IO).launch {
            val api = FreeToGameApi.getInstance()
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
        //MENU
        supportActionBar?.title = game.title
        supportActionBar?.subtitle = game.developer

        //HERO
            //Video
        binding.trailerVideoView.setVideoURI("https://www.freetogame.com/g/${game.id}/videoplayback.webm".toUri())
        binding.trailerVideoView.setOnPreparedListener {
            CoroutineScope(Dispatchers.IO).launch {
                delay(3000.milliseconds)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.thumbnailImageView.visibility=View.GONE
                    binding.trailerVideoView.start()
                }
            }
        }
        binding.trailerVideoView.setOnCompletionListener {
            binding.thumbnailImageView.visibility = View.VISIBLE
        }
            //Image
        Picasso.get().load(game.thumbnail).into(binding.thumbnailImageView)

        //BASIC INFO
        binding.titleTextView.text = game.title
        binding.genreChip.text = game.genre
        Log.i("API REST",game.platform)
        when(game.platform){
            "Windows, Web Browser" ->{
                binding.platformPCImageView.setImageResource(R.drawable.ic_platform_pc)
                binding.platformPCImageView.visibility = View.VISIBLE
                binding.platformWebImageView.setImageResource(R.drawable.ic_platform_web)
                binding.platformWebImageView.visibility = View.VISIBLE
            }
            "Windows"->  {
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
        binding.showMoreTextView.setOnClickListener {
            if(showMoreEnabled){
                binding.descriptionTextView.maxLines = 5
                binding.showMoreTextView.text = getString(R.string.detail_label_show_more)
            }else{
                binding.descriptionTextView.maxLines = Int.MAX_VALUE
                binding.showMoreTextView.text = getString(R.string.detail_label_show_less)
            }
            showMoreEnabled = !showMoreEnabled
        }

        // Screenshots
        val adapter = GalleryAdapter(game.screenshots ?: emptyList(),-1){position ->
            intent.putExtra(GalleryActivity.EXTRA_SCREENSHOT_INDEX,position)
            intent.putExtra(GalleryActivity.EXTRA_SCREENSHOTS_ARRAY,game.screenshots.map{it.image}.toTypedArray())
        }
        binding.screenshotsRecyclerView.adapter = adapter
        //TODO el codigo que manda al linear layout

        // System requirements
        binding.systemRequirementsOsTextView.text = game.systemRequirements?.os?: getString(R.string.detail_without_data)
        binding.systemRequirementsProcessorTextView.text = game.systemRequirements?.processor?: getString(R.string.detail_without_data)
        binding.systemRequirementsMemoryTextView.text = game.systemRequirements?.memory?: getString(R.string.detail_without_data)
        binding.systemRequirementsGraphicsTextView.text = game.systemRequirements?.graphics?: getString(R.string.detail_without_data)
        binding.systemRequirementsStorageTextView.text = game.systemRequirements?.storage?: getString(R.string.detail_without_data)

        // Buttons
        binding.playButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(game.gameUrl.toUri())
            startActivity(intent)
        }
        binding.shareButton.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.detail_share)}: ${game.profileUrl}.")
            sendIntent.type = "text/plain"

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}