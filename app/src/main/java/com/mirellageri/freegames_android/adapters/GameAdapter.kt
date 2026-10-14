package com.mirellageri.freegames_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirellageri.freegames_android.R
import com.mirellageri.freegames_android.data.Game
import com.mirellageri.freegames_android.databinding.ItemGameBinding
import com.squareup.picasso.Picasso

class GameAdapter(
    var items: List<Game>,
    val onItemClick:(position : Int) -> Unit
): RecyclerView.Adapter<GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGameBinding.inflate(layoutInflater,parent,false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = items[position]
        holder.render(game)
        holder.binding.gameCard.setOnClickListener {
            onItemClick(position)
        }
    }
    override fun getItemCount(): Int =items.size
    fun updateData(dataSet: List<Game>){
        this.items = dataSet
        notifyDataSetChanged()
    }
}

class GameViewHolder(val binding: ItemGameBinding ): RecyclerView.ViewHolder(binding.root){
    //pasasr un dato para luego mostrarlo en pantalla
    //si queremos usar el binding debemos convertirlo a propiedad con var o val porque sino solo se usa en la contruccion y luego se desecha
    fun render(game: Game){
        binding.titleTextView.text = game.title
        binding.descriptionTextView.text = game.shortDescription
        binding.genreChip.text = game.genre

        val platformResId = if(game.platform.contains("Windows")){
            R.drawable.ic_platform_pc
        }else{
            R.drawable.ic_platform_web
        }
        binding.platformImageView.setImageResource(platformResId)
        Picasso.get().load(game.thumbnail).into(binding.thumbnailImageView)
    }
}